package com.fish_diseases.api_gateway;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.fish_diseases.api_gateway.microservices.ApiGatewayFishesTests;
import com.fish_diseases.api_gateway.microservices.ApiGatewayLaboratoryMethodsTests;
import com.fish_diseases.api_gateway.microservices.ApiGatewayParasitesTests;
import com.fish_diseases.api_gateway.microservices.ApiGatewayTreatmentsTests;
import com.fish_diseases.api_gateway.microservices.ApiGatewayUsersTests;

/**
 * Suite de pruebas de integración para el API Gateway. Incluye tests de los
 * microservicios de Usuarios, Parásitos, Peces, Tratamientos y Métodos de
 * Laboratorio.
 */
@Suite
@IncludeEngines("junit-jupiter")
@SelectClasses({
		ApiGatewayUsersTests.class,
		ApiGatewayParasitesTests.class,
		ApiGatewayFishesTests.class,
		ApiGatewayTreatmentsTests.class,
		ApiGatewayLaboratoryMethodsTests.class
})
public class ApiGatewayApplicationTests {

}
