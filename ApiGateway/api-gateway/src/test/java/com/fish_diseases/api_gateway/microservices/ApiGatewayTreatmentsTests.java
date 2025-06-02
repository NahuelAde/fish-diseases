package com.fish_diseases.api_gateway.microservices;

import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fish_diseases.api_gateway.config.BaseApiGatewayTest;

/**
 * Conjunto de tests parametrizados para los endpoints de tratamientos del API
 * Gateway, leyendo escenarios desde un CSV.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiGatewayTreatmentsTests extends BaseApiGatewayTest {

	/**
	 * URI base de los endpoints de tratamientos.
	 */
	private static final String TREATMENT_URI = "/treatment-service/treatments";

	/**
	 * Ejecuta cada caso de prueba definido en el CSV para tratamientos.
	 *
	 * @param method         Método HTTP a testear
	 * @param rawUri         URI relativa del endpoint de tratamientos
	 * @param role           Rol con el que se autentica en la llamada
	 * @param params         Parámetros que influyen en el cuerpo de la petición
	 * @param expectedStatus Código HTTP esperado en la respuesta
	 */
	@ParameterizedTest
	@CsvFileSource(resources = "/endpoints-treatments.csv", numLinesToSkip = 1)
	void testTreatmentEndpoints(String method, String rawUri, String role, String params, int expectedStatus) {
		executeTest(method, rawUri, role, params, expectedStatus);
	}

	/**
	 * Construye el cuerpo de las peticiones POST para tratamientos según el caso de
	 * prueba.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Descripción del escenario
	 * @return Mapa con datos JSON para crear un tratamiento o vacío
	 */
	@Override
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		if (rawUri.startsWith(TREATMENT_URI)) {
			return buildTreatmentPost(params);
		}
		return super.buildBodyPost(rawUri, params);
	}

	/**
	 * Construye el cuerpo de las peticiones PATCH para tratamientos según el caso
	 * de prueba.
	 *
	 * @param rawUri Ruta del endpoint invocado
	 * @param params Descripción del escenario
	 * @return Mapa con datos JSON para actualizar un tratamiento o vacío
	 */
	@Override
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		if (rawUri.startsWith(TREATMENT_URI)) {
			return buildTreatmentPatch(params);
		}
		return super.buildBodyPatch(rawUri, params);
	}

	/**
	 * Crea payloads específicos para POST /treatments basados en el parámetro.
	 *
	 * @param params Caso de prueba que define el contenido del JSON
	 * @return Mapa con campos para la petición POST o vacío si no aplica
	 */
	private Map<String, ?> buildTreatmentPost(String params) {
		switch (params) {
		case "complete data":
			return Map
					.of(
							"name", "Tratamiento autotest",
							"description", "Tratamiento para tests automáticos.");
		case "incomplete data":
			return Map.of("name", "");
		case "data exists":
			return buildTreatmentPost("complete data");
		default:
			return Map.of();
		}
	}

	/**
	 * Crea payloads específicos para PATCH /treatments basados en el parámetro.
	 *
	 * @param params Caso de prueba que define el contenido del JSON
	 * @return Mapa con campos para la petición PATCH o vacío si no aplica
	 */
	private Map<String, ?> buildTreatmentPatch(String params) {
		switch (params) {
		case "complete data":
			return Map
					.of(
							"description", "Tratamiento para tests automáticos actualizado.");
		case "incomplete data":
			return Map
					.of(
							"description", "");
		default:
			return Map.of();
		}
	}
}
