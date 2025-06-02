package com.fish_diseases.api_gateway.microservices;

import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.fish_diseases.api_gateway.config.BaseApiGatewayTest;

/**
 * Conjunto de tests parametrizados para los endpoints de métodos de laboratorio
 * del API Gateway, leyendo escenarios desde un CSV.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiGatewayLaboratoryMethodsTests extends BaseApiGatewayTest {
	
    /**
     * URI base de los endpoints de métodos de laboratorio.
     */
	private static final String LABORATORY_METHOD_URI = "/treatment-service/laboratory-methods";

    /**
     * Ejecuta cada caso de prueba definido en el CSV para métodos de laboratorio.
     *
     * @param method         Método HTTP a testear
     * @param rawUri         URI relativa del endpoint de métodos de laboratorio
     * @param role           Rol con el que se autentica en la llamada
     * @param params         Parámetros que influyen en el cuerpo de la petición
     * @param expectedStatus Código HTTP esperado en la respuesta
     */
	@ParameterizedTest
	@CsvFileSource(resources = "/endpoints-laboratoryMethods.csv", numLinesToSkip = 1)
	void testLaboratoryMethodsEndpoints(String method, String rawUri, String role, String params, int expectedStatus) {
		executeTest(method, rawUri, role, params, expectedStatus);
	}

    /**
     * Construye el cuerpo de las peticiones POST para métodos de laboratorio
     * según el caso de prueba.
     *
     * @param rawUri Ruta del endpoint invocado
     * @param params Descripción del escenario
     * @return Mapa con datos JSON para crear un método de laboratorio o vacío
     */
	@Override
	protected Map<String, ?> buildBodyPost(String rawUri, String params) {
		if (rawUri.startsWith(LABORATORY_METHOD_URI)) {
			return buildLaboratoryMethodPost(params);
		}
		return super.buildBodyPost(rawUri, params);
	}

    /**
     * Construye el cuerpo de las peticiones PATCH para métodos de laboratorio
     * según el caso de prueba.
     *
     * @param rawUri Ruta del endpoint invocado
     * @param params Descripción del escenario
     * @return Mapa con datos JSON para actualizar un método de laboratorio o vacío
     */
	@Override
	protected Map<String, ?> buildBodyPatch(String rawUri, String params) {
		if (rawUri.startsWith(LABORATORY_METHOD_URI)) {
			return buildLaboratoryMethodPatch(params);
		}
		return super.buildBodyPatch(rawUri, params);
	}

    /**
     * Crea payloads específicos para POST /laboratory-methods basados en el parámetro.
     *
     * @param params Caso de prueba que define el contenido del JSON
     * @return Mapa con campos para la petición POST o vacío si no aplica
     */
	private Map<String, ?> buildLaboratoryMethodPost(String params) {
		switch (params) {
		case "complete data":
			return Map
					.of(
							"name", "Método laboratorio autotest",
							"technique", "Método de laboratorio para tests automáticos.");
		case "incomplete data":
			return Map.of("name", "");
		case "data exists":
			return buildLaboratoryMethodPost("complete data");
		default:
			return Map.of();
		}
	}

    /**
     * Crea payloads específicos para PATCH /laboratory-methods basados en el parámetro.
     *
     * @param params Caso de prueba que define el contenido del JSON
     * @return Mapa con campos para la petición PATCH o vacío si no aplica
     */
	private Map<String, ?> buildLaboratoryMethodPatch(String params) {
		switch (params) {
		case "complete data":
			return Map
					.of(
							"technique", "Método de laboratorio para tests automáticos actualizado.");
		case "incomplete data":
			return Map
					.of(
							"technique", "");
		default:
			return Map.of();
		}
	}
}
