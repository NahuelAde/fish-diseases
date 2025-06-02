package com.fish_diseases.api_gateway.microservices;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fish_diseases.api_gateway.config.BaseApiGatewayTest;

/**
 * Conjunto de tests parametrizados para los endpoints de parásitos del API
 * Gateway, leyendo escenarios desde un CSV.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiGatewayParasitesTests extends BaseApiGatewayTest {

	/**
	 * URI base de los endpoints de parásitos.
	 */
	private static final String PARASITE_URI = "/biodata-service/parasites";

	/**
	 * Ejecuta cada caso de prueba definido en el CSV para parásitos.
	 *
	 * @param method         Método HTTP a testear
	 * @param rawUri         URI relativa del endpoint de parásitos
	 * @param role           Rol con el que se autentica en la llamada
	 * @param params         Parámetros que influyen en el cuerpo de la petición
	 * @param expectedStatus Código HTTP esperado en la respuesta
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/endpoints-parasites.csv", numLinesToSkip = 1)
	void testParasiteEndpoints(String method, String rawUri, String role, String params, int expectedStatus) {
		executeTest(method, rawUri, role, params, expectedStatus);
	}

	/**
	 * Construye el cuerpo de las peticiones POST para parásitos según el caso de
	 * prueba.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Descripción del escenario
	 * @return Mapa con datos JSON para crear un parásito o vacío
	 */
	@Override
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		if (rawUri.startsWith(PARASITE_URI)) {
			return buildParasitePost(params);
		}
		return super.buildBodyPost(rawUri, params);
	}

	/**
	 * Construye el cuerpo de las peticiones PATCH para parásitos según el caso de
	 * prueba.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Descripción del escenario
	 * @return Mapa con datos JSON para actualizar un parásito o vacío
	 */
	@Override
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		if (rawUri.startsWith(PARASITE_URI)) {
			return buildParasitePatch(params);
		}
		return super.buildBodyPatch(rawUri, params);
	}

	/**
	 * Crea payloads específicos para POST /parasites basados en el parámetro.
	 *
	 * @param params Caso de prueba que define el contenido del JSON
	 * @return Mapa con campos para la petición POST o vacío si no aplica
	 */
	private Map<String, ?> buildParasitePost(String params) {
		return switch (params) {
		case "complete data" -> Map
				.of(
						"scientificName", "Autotest parasiticus",
						"diagnosticFeatures", "Se encuentra en los tests automáticos",
						"fishes", List.of("Piscis autotestus"),
						"locationOnHost", "Agallas");
		case "incomplete data" -> Map
				.of(
						"scientificName", "Autotest parasiticus");
		case "data exists" -> buildParasitePost("complete data");
		default -> Map.of();
		};
	}

	/**
	 * Crea payloads específicos para PATCH /parasites basados en el parámetro.
	 *
	 * @param params Caso de prueba que define el contenido del JSON
	 * @return Mapa con campos para la petición PATCH o vacío si no aplica
	 */
	private Map<String, ?> buildParasitePatch(String params) {
		return switch (params) {
		case "complete data" -> Map
				.of(
						"commonName", "Parásito Autotest");
		default -> Map.of();
		};
	}
}
