package com.fish_diseases.api_gateway.microservices;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fish_diseases.api_gateway.config.BaseApiGatewayTest;

/**
 * Conjunto de tests parametrizados para los endpoints de peces
 * del API Gateway, leyendo escenarios desde un CSV.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiGatewayFishesTests extends BaseApiGatewayTest {

    /**
     * URI base de los endpoints de peces.
     */
	private static final String FISH_URI = "/biodata-service/fishes";

    /**
     * Ejecuta cada caso de prueba definido en el CSV para peces.
     *
     * @param method         Método HTTP a testear
     * @param rawUri         URI relativa del endpoint de peces
     * @param role           Rol con el que se autentica en la llamada
     * @param params         Parámetros que influyen en el cuerpo de la petición
     * @param expectedStatus Código HTTP esperado en la respuesta
     */
	@ParameterizedTest
	@CsvFileSource(resources = "/endpoints-fishes.csv", numLinesToSkip = 1)
	void testFishEndpoints(String method, String rawUri, String role, String params, int expectedStatus) {
		executeTest(method, rawUri, role, params, expectedStatus);
	}

    /**
     * Construye el cuerpo de las peticiones POST para peces
     * según el caso de prueba.
     *
     * @param rawUri Ruta del endpoint invocado
     * @param params Descripción del escenario
     * @return Mapa con datos JSON para crear un pez o vacío
     */
	@Override
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		if (rawUri.startsWith(FISH_URI)) {
			return buildFishPost(params);
		}
		return super.buildBodyPost(rawUri, params);
	}

	/**
     * Construye el cuerpo de las peticiones PATCH para peces
     * según el caso de prueba.
     *
     * @param rawUri Ruta del endpoint invocado
     * @param params Descripción del escenario
     * @return Mapa con datos JSON para actualizar un pez o vacío
     */
	@Override
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		if (rawUri.startsWith(FISH_URI)) {
			return buildFishPatch(params);
		}
		return super.buildBodyPatch(rawUri, params);
	}

    /**
     * Crea payloads específicos para POST /fishes basados en el parámetro.
     *
     * @param params Caso de prueba que define el contenido del JSON
     * @return Mapa con campos para la petición POST o vacío si no aplica
     */
	private Map<String, ?> buildFishPost(String params) {
		return switch (params) {
		case "complete data" -> Map
				.of(
						"scientificName", "Autotestus fishis",
						"parasites", List.of());
		case "incomplete data" -> Map.of("scientificName", "");
		case "data exists" -> buildFishPost("complete data");
		default -> Map.of();
		};
	}

    /**
     * Crea payloads específicos para PATCH /fishes basados en el parámetro.
     *
     * @param params Caso de prueba que define el contenido del JSON
     * @return Mapa con campos para la petición PATCH o vacío si no aplica
     */
	private Map<String, ?> buildFishPatch(String params) {
		return switch (params) {
		case "complete data" -> Map.of("commonName", "Pez Autotest");
		default -> Map.of();
		};
	}
}
