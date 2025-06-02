package com.fish_diseases.auth_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configura la especificación OpenAPI para el servicio de autenticación de usuarios en FishDiseases.
 *
 * Define los metadatos de la documentación de la API, como información del desarrollador, 
 * descripción del servicio, versión de la API y servidores disponibles. 
 * También establece el esquema de seguridad necesario para la autenticación basada en JWT (JSON Web Tokens).
 */
@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Dev Nahuel Ade",
						email = "contact@developments-dna.com",
						url = "https://developments-dna.com"
				),
				description = "Documentación OpenApi para autenticación de usuarios en FishDiseases",
				title = "Especificaciones OpenApi - Dev Nahuel Ade",
				version = "1.0.0"
		),
		servers = {
		        @Server(
		                description = "API Gateway Routing",
		                url = "http://localhost:9000/auth-service"
		        ),
				@Server(
						description = "Local ENV",
						url = "http://localhost:8888"
				)
		},
	    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
		name = "bearerAuth",
		description = "Descripción de autenticación JWT",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
