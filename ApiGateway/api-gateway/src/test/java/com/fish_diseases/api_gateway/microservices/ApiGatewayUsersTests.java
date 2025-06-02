package com.fish_diseases.api_gateway.microservices;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fish_diseases.api_gateway.config.BaseApiGatewayTest;

/**
 * Conjunto de tests parametrizados para los endpoints de usuarios del API
 * Gateway, leyendo escenarios desde un CSV.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiGatewayUsersTests extends BaseApiGatewayTest {

	/**
	 * Parámetro que indica la URI base de usuarios.
	 */
	private static final String USERS_URI = "/auth-service/users";

	/**
	 * Guarda el rol actual para adaptar la creación de payloads dinámicos.
	 */
	private String currentRole;

	/**
	 * Ejecuta cada caso de prueba definido en el CSV, invocando executeTest().
	 *
	 * @param method         Método HTTP a testear (GET, POST, etc.)
	 * @param rawUri         URI relativa del endpoint
	 * @param role           Rol con el que se autentica en la llamada
	 * @param params         Parámetros que influyen en el cuerpo o token
	 * @param expectedStatus Código HTTP esperado en la respuesta
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/endpoints-users.csv", numLinesToSkip = 1)
	void testUserEndpoints(String method, String rawUri, String role, String params, int expectedStatus) {
		this.currentRole = role;
		executeTest(method, rawUri, role, params, expectedStatus);
	}

	/**
	 * Construye el cuerpo de las peticiones POST para usuarios, delegando según el
	 * endpoint (register, login, logout).
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Parámetros que determinan la carga útil
	 * @return Mapa con datos JSON o el comportamiento por defecto
	 */
	@Override
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		if (rawUri.startsWith(USERS_URI)) {
			return buildUserPost(rawUri, params);
		}
		return super.buildBodyPost(rawUri, params);
	}

	/**
	 * Construye el cuerpo de las peticiones PUT para usuarios, para endpoints de
	 * deshabilitar, asignar rol o actualizar campos.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Parámetros que determinan la carga útil
	 * @return Mapa con datos JSON o el comportamiento por defecto
	 */
	@Override
	protected Map<String, ?> buildBodyPut(String rawUri, String params) {
		if (rawUri.startsWith(USERS_URI)) {
			return buildUserPut(rawUri, params);
		}
		return super.buildBodyPut(rawUri, params);
	}

	/**
	 * Construye el cuerpo de las peticiones PATCH para actualizar usuarios,
	 * adaptándose a los distintos roles y tipos de datos.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Parámetros que determinan la carga útil
	 * @return Mapa con datos JSON o el comportamiento por defecto
	 */
	@Override
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		if (rawUri.startsWith(USERS_URI)) {
			return buildUserPatch(rawUri, params);
		}
		return super.buildBodyPatch(rawUri, params);
	}

	///////////////////////
	/// BUILD USER POST ///
	///////////////////////

	/**
	 * Crea payloads específicos para /auth-service/users/register, /login y
	 * /logout.
	 *
	 * @param rawUri Ruta completa invocada
	 * @param params Descripción del caso (p.ej. "complete data", "user+pass")
	 * @return Mapa con campos para la petición o vacío si no aplica
	 */
	private Map<String, ?> buildUserPost(String rawUri, String params) {
		if (!rawUri.startsWith(USERS_URI)) {
			return Map.of();
		}
		if (rawUri.equals("/auth-service/users/register")) {
			switch (params) {
			case "complete data":
				return Map
						.of(
								"firstname", "Prueba",
								"lastname", "Autotest",
								"username", "autotestuser",
								"password", "P4ssw0rd!",
								"nationalId", "1111111A",
								"roles", List
										.of(
												Map
														.of(
																"roleId", 1073741824,
																"name", "ROLE_TREATMENT")),
								"city", "string",
								"country", "string",
								"email", "test@example.com",
								"enabled", true);
			case "incomplete data":
				return Map
						.of(
								"firstname", "",
								"lastname", "",
								"username", "",
								"password", "",
								"nationalId", "",
								"roles", List.of(),
								"city", "",
								"country", "",
								"email", "",
								"enabled", true);
			case "data exists":
				return buildUserPost("/auth-service/users/register", "complete data");
			case "data exists treatment":
				return Map
						.of(
								"firstname", "Treatment",
								"lastname", "User",
								"username", "treatment",
								"password", "Treat123*",
								"nationalId", "Y1234567Z",
								"roles", List
										.of(
												Map.of("roleId", 1073741825, "name", "ROLE_TREATMENT")),
								"city", "Barcelona",
								"country", "España",
								"email", "treatment@example.com",
								"enabled", true);
			case "data exists admin":
				return Map
						.of(
								"firstname", "Nahuel Marcelo",
								"lastname", "Ade",
								"username", "admin",
								"password", "Admin123*",
								"nationalId", "X0859212R",
								"roles", List
										.of(
												Map.of("roleId", 1073741824, "name", "ROLE_ADMIN")),
								"city", "Madrid",
								"country", "España",
								"email", "nahuel.ade.pro@gmail.com",
								"enabled", true);
			default:
				return Map.of();
			}
		}
		if (rawUri.equals("/auth-service/users/login")) {
			switch (params) {
			case "user+pass":
				return Map.of("username", "autotestuser", "password", "P4ssw0rd!");
			case "treatment+pass":
				return Map.of("username", "treatment", "password", "Treat123*");
			case "admin+pass":
				return Map.of("username", "admin", "password", "Admin123*");
			case "incomplete data":
				return Map.of("username", "");
			default:
				return Map.of();
			}
		}
		if (rawUri.equals("/auth-service/users/logout")) {
			return Map.of();
		}
		return Map.of();
	}

	//////////////////////
	/// BUILD USER PUT ///
	//////////////////////

	/**
	 * Genera el cuerpo de PUT para /auth-service/users/{id}/disable y
	 * /auth-service/users/{id}/role-admin.
	 *
	 * @param rawUri Ruta completa invocada
	 * @param params Parámetros adicionales para personalizar la petición
	 * @return Mapa con datos JSON o vacío si no aplica
	 */
	private Map<String, ?> buildUserPut(String rawUri, String params) {
		String prefix = USERS_URI + "/";
		if (!rawUri.startsWith(prefix)) {
			return Map.of();
		}
		String[] parts = rawUri.substring(prefix.length()).split("/");
		if (parts.length == 0)
			return Map.of();

		if (rawUri.contains("/disable")) {
			return Map.of();
		}

		if (rawUri.contains("/role-admin")) {
			return Map.of();
		}

		if (params.startsWith("userId:")) {
			return Map.of("email", "updated@example.com");
		}

		if ("incomplete data".equals(params)) {
			return Map.of("email", "test");
		}

		return Map.of();
	}

	////////////////////////
	/// BUILD USER PATCH ///
	////////////////////////

	/**
	 * Genera el cuerpo de PATCH para /auth-service/users/{id}, variando según rol
	 * (TREATMENT vs ADMIN) e integridad de datos.
	 *
	 * @param rawUri Ruta completa invocada
	 * @param params Parámetros que describen el caso de prueba
	 * @return Mapa con campos actualizados o vacío si no corresponde
	 */
	private Map<String, ?> buildUserPatch(String rawUri, String params) {
		String prefix = USERS_URI + "/";
		if (!rawUri.startsWith(prefix)) {
			return Map.of();
		}
		String idStr = rawUri.substring(prefix.length());
		int userId;
		try {
			userId = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			return Map.of();
		}

		if ("TREATMENT".equals(currentRole)) {
			if (userId == 2) {
				if ("userId:2".equals(params)) {
					return Map.of("email", "treatment.updated@example.com", "enabled", true);
				}
				if ("incomplete data".equals(params)) {
					return Map.of("email", "treat", "enabled", true);
				}
			}
			return Map.of();
		}

		if ("ADMIN".equals(currentRole)) {
			if ("incomplete data".equals(params)) {
				return Map.of("email", "test", "enabled", true);
			}
			if (userId == 2) {
				return Map.of("email", "treatment.updated.by.admin@example.com", "enabled", true);
			}
			return Map.of("email", "admin.updated@example.com", "enabled", true);
		}
		return Map.of();
	}
}
