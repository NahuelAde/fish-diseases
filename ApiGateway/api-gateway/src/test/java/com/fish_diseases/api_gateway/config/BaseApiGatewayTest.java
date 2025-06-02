package com.fish_diseases.api_gateway.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fish_diseases.api_gateway.security.TestJwtHelper;

/**
 * Clase base para los tests de integración del API Gateway. Proporciona
 * configuración de servidor, cliente HTTP y utilidades comunes para ejecutar
 * pruebas de distintos endpoints a través de WebTestClient.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseApiGatewayTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected TestJwtHelper testJwtHelper;

	protected WebTestClient webClient;

	protected static final String USERS_URI = "/auth-service/users";
	protected static final String PARASITE_URI = "/biodata-service/parasites";
	protected static final String FISH_URI = "/biodata-service/fishes";
	protected static final String TREATMENT_URI = "/treatment-service/treatments";
	protected static final String LABORATORY_METHOD_URI = "/treatment-service/laboratory-methods";

	protected static final String ANSI_GREEN = "\u001B[32m";
	protected static final String ANSI_RED = "\u001B[31m";
	protected static final String ANSI_YELLOW = "\u001B[33m";
	protected static final String ANSI_RESET = "\u001B[0m";

	/**
	 * Lista de URIs invocadas durante todos los tests.
	 */
	protected static final List<String> uris = new ArrayList<>();

	/**
	 * Resultados formateados de cada invocación (código, URI, rol, OK/ERROR).
	 */
	protected static final List<String> results = new ArrayList<>();

	/**
	 * Estado de la base de datos de usuarios tras cada prueba.
	 */
	protected static final List<String> dbStates = new ArrayList<>();

	/**
	 * Inicializa el WebTestClient antes de cada test, apuntando al servidor
	 * embebido en el puerto aleatorio.
	 */
	@BeforeEach
	void setUp() {
		this.webClient = WebTestClient
				.bindToServer()
				.baseUrl("http://localhost:" + port)
				.responseTimeout(Duration.ofSeconds(120))
				.build();
	}

	/**
	 * Ejecuta una petición HTTP contra el API Gateway, construye token si el rol lo
	 * requiere, envía la petición y comprueba que el código de respuesta coincide
	 * con el esperado.
	 *
	 * @param method         Método HTTP: GET, POST, PUT, PATCH o DELETE
	 * @param rawUri         Ruta relativa del endpoint a invocar
	 * @param role           Rol del usuario para el JWT: USER, TREATMENT o ADMIN
	 * @param params         Parámetros adicionales para cuerpo o token
	 * @param expectedStatus Código HTTP que esperamos recibir
	 * @throws IllegalArgumentException si se pasa un método HTTP no soportado
	 */
	protected void executeTest(String method, String rawUri, String role, String params, int expectedStatus) {
		String uri = "http://localhost:" + port + rawUri;

		String token = "";
		boolean isUserRole = "USER".equals(role);
		boolean isUserServiceCall = rawUri.startsWith(USERS_URI);

		if (!isUserRole || !isUserServiceCall) {
			if ("TREATMENT".equals(role) || "ADMIN".equals(role)) {
				token = buildAuthHeader(role, params);
			}
		}

		try {
			var responseSpec = switch (method) {
			case "GET" -> webClient.get().uri(uri).header("Authorization", token).exchange();
			case "POST" -> webClient
					.post()
					.uri(uri)
					.header("Authorization", token)
					.bodyValue(buildBodyPost(rawUri, params))
					.exchange();
			case "PUT" -> webClient
					.put()
					.uri(uri)
					.header("Authorization", token)
					.bodyValue(buildBodyPut(rawUri, params))
					.exchange();
			case "PATCH" -> webClient
					.patch()
					.uri(uri)
					.header("Authorization", token)
					.bodyValue(buildBodyPatch(rawUri, params))
					.exchange();
			case "DELETE" -> webClient.delete().uri(uri).header("Authorization", token).exchange();
			default -> throw new IllegalArgumentException("Método no soportado: " + method);
			};

			int actualStatus = responseSpec.returnResult(Void.class).getStatus().value();
			boolean success = actualStatus == expectedStatus;
			String color = success ? ANSI_GREEN : ANSI_RED;

			String line = String
					.format(
							"%s%-8s %-68s con rol %12s → esperado:%3d, real:%3d %s%s",
							color, method, rawUri, role, expectedStatus, actualStatus, success ? "OK" : "ERROR",
							ANSI_RESET);

			uris.add(rawUri);
			results.add(line);
			System.out.println(line);

			assertEquals(expectedStatus, actualStatus,
					() -> "Esperaba " + expectedStatus + " pero fue " + actualStatus + " en " + method + " " + rawUri);
		} finally {
			printUserDatabaseState();
		}
	}

	/**
	 * Genera el encabezado Authorization con un JWT válido en función del rol y los
	 * parámetros de usuario.
	 *
	 * @param role   Rol del usuario: ADMIN, TREATMENT o USER
	 * @param params Parámetros para determinar el nombre de usuario
	 * @return Cabecera "Bearer <token>"
	 */
	protected String buildAuthHeader(String role, String params) {
		String username = switch (params) {
		case "userId:1" -> "admin";
		case "userId:2" -> "treatment";
		case "userId:3" -> "autotestuser";
		default -> role.toLowerCase();
		};

		List<String> roles = switch (role) {
		case "ADMIN" -> List.of("ROLE_ADMIN");
		case "TREATMENT" -> List.of("ROLE_TREATMENT");
		default -> List.of();
		};

		String token = testJwtHelper.createToken(username, roles);
		return "Bearer " + token;
	}

	/**
	 * Al final de todos los tests, imprime en consola un resumen agrupado por
	 * microservicio con los resultados de cada llamada.
	 */
	@AfterAll
	void printSummary() {
		System.out.println("\n\n========================= Resumen de pruebas de integración =========================");
		String lastPrefix = "";

		for (int i = 0; i < results.size(); i++) {
			String uri = uris.get(i);
			String prefix;
			if (uri.startsWith(USERS_URI))
				prefix = "      USERS       ";
			else if (uri.startsWith(PARASITE_URI))
				prefix = "    PARASITES     ";
			else if (uri.startsWith(FISH_URI))
				prefix = "     FISHES       ";
			else if (uri.startsWith(TREATMENT_URI))
				prefix = "    TREATMENTS    ";
			else if (uri.startsWith(LABORATORY_METHOD_URI))
				prefix = "LABORATORY METHODS";
			else
				prefix = "      OTHER       ";

			if (!prefix.equals(lastPrefix)) {
				System.out.println("\n----------------------------------------------------------------------------");
				System.out.println("---------------------------- " + prefix + " ----------------------------");
				System.out.println("----------------------------------------------------------------------------");
				lastPrefix = prefix;
			}
			System.out.println(results.get(i));
			System.out.print(dbStates.get(i));
		}

		System.out.println("\n=====================================================================================\n");
	}

	/**
	 * Consulta el estado actual de la tabla de usuarios mediante GET
	 * /auth-service/users y lo formatea para incluirlo en el log de cada prueba.
	 */
	protected void printUserDatabaseState() {
		String uri = USERS_URI;
		String token = buildAuthHeader("ADMIN", "userId:1");

		var result = webClient
				.get()
				.uri(uri)
				.header("Authorization", token)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(new ParameterizedTypeReference<List<Map<String, Object>>>() {
				})
				.returnResult();

		List<Map<String, Object>> response = result.getResponseBody();

		StringBuilder dbState = new StringBuilder();
		dbState.append(ANSI_YELLOW).append("-> DB State:").append(ANSI_RESET).append("\n");

		if (response == null || response.isEmpty()) {
			dbState.append(ANSI_YELLOW).append("   [vacía]").append(ANSI_RESET).append("\n");
		} else {
			for (Map<String, Object> user : response) {
				Object id = user.get("userId");
				if (id == null)
					id = user.get("id");
				Object username = user.get("username");
				Object enabled = user.get("enabled");
				Object rolesObj = user.get("roles");
				String rolesStr = "";
				if (rolesObj instanceof List<?> rolesList) {
					rolesStr = rolesList.stream().map(Object::toString).collect(Collectors.joining(","));
				} else if (rolesObj != null) {
					rolesStr = rolesObj.toString();
				}

				dbState
						.append(String
								.format(ANSI_YELLOW + "   %-3s - %-15s - enabled:%-7s - roles:%-20s" + ANSI_RESET
										+ "%n", id, username, enabled, rolesStr));
			}
		}

		String dbStateStr = dbState.toString();
		System.out.print(dbStateStr);
		dbStates.add(dbStateStr);
	}

	/**
	 * Construye el cuerpo de la petición POST según el endpoint invocado. Por
	 * defecto devuelve un mapa vacío; puede overridearse en subclases.
	 *
	 * @param rawUri Ruta relativa del endpoint
	 * @param params Parámetros adicionales
	 * @return Mapa con los campos JSON a enviar
	 */
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		return Map.of();
	}

	/**
	 * Construye el cuerpo de la petición PUT según el endpoint invocado. Por
	 * defecto devuelve un mapa vacío; puede overridearse en subclases.
	 *
	 * @param rawUri Ruta relativa del endpoint
	 * @param params Parámetros adicionales
	 * @return Mapa con los campos JSON a enviar
	 */
	protected Map<String, ?> buildBodyPut(String rawUri, String params) {
		return Map.of();
	}

	/**
	 * Construye el cuerpo de la petición PATCH según el endpoint invocado. Por
	 * defecto devuelve un mapa vacío; puede overridearse en subclases.
	 *
	 * @param rawUri Ruta relativa del endpoint
	 * @param params Parámetros adicionales
	 * @return Mapa con los campos JSON a enviar
	 */
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		return Map.of();
	}
}
