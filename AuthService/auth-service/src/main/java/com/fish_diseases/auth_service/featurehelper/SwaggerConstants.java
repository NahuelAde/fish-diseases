package com.fish_diseases.auth_service.featurehelper;

/**
 * Clase de utilidades que contiene constantes relacionadas con los códigos y descripciones de respuesta HTTP
 * utilizados en la documentación y especificaciones de la API Swagger para el servicio de autenticación.
 */
public class SwaggerConstants {

	private SwaggerConstants() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final String OK_CODE = "200";
	
	public static final String OK_DESCRIPTION = "Respuesta correcta";
	
	public static final String CREATED_CODE = "201";
	
	public static final String CREATED_DESCRIPTION = "Elemento creado";
	
	public static final String NO_CONTENT_CODE = "204";
	
	public static final String NO_CONTENT_DESCRIPTION = "Sin contenido";
	
	public static final String BAD_REQUEST_CODE = "400";
	
	public static final String BAD_REQUEST_DESCRIPTION = "Error de validación";

	public static final String UNAUTHORIZED_CODE = "401";
	
	public static final String UNAUTHORIZED_DESCRIPTION = "No autorizado";

	public static final String FORBIDDEN_CODE = "403";
	
	public static final String FORBIDDEN_DESCRIPTION = "Prohibido";

	public static final String NOT_FOUND_CODE = "404";
	
	public static final String NOT_FOUND_DESCRIPTION = "No encontrado";

	public static final String CONFLICT_CODE = "409";
	
	public static final String CONFLICT_DESCRIPTION = "Recurso ya existe";

	public static final String INTERNAL_SERVER_ERROR_CODE = "500";
	
	public static final String INTERNAL_SERVER_ERROR_DESCRIPTION = "Error de sistema";

}
