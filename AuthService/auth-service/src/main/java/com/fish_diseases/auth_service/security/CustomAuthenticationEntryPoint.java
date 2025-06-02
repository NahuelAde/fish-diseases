package com.fish_diseases.auth_service.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fish_diseases.auth_service.utils.ResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que implementa el punto de entrada para gestionar los errores de autenticación.
 * Esta clase se encarga de devolver respuestas adecuadas cuando se produce una
 * excepción de autenticación, como credenciales incorrectas o acceso no autorizado.
 * 
 * Utiliza el servicio {@link ResponseUtil} para generar las respuestas de error
 * y proporciona un formato JSON adecuado para las respuestas de error de autenticación.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private final ResponseUtil responseUtil;
	private final ObjectMapper objectMapper;

    /**
     * Constructor de la clase {@link CustomAuthenticationEntryPoint}.
     * 
     * @param responseUtil Utilidad para generar respuestas de error.
     * @param objectMapper Manejador de conversión de objetos a JSON.
     */
	public CustomAuthenticationEntryPoint(ResponseUtil responseUtil, ObjectMapper objectMapper) {
		this.responseUtil = responseUtil;
		this.objectMapper = objectMapper;
	}

    /**
     * Método que se invoca cuando se detecta un error de autenticación.
     * Dependiendo del tipo de excepción, se genera una respuesta adecuada
     * con el estado HTTP correspondiente y un mensaje de error en formato JSON.
     * 
     * @param request La solicitud HTTP que originó el error de autenticación.
     * @param response La respuesta HTTP que será enviada al cliente.
     * @param authException La excepción de autenticación que se ha producido.
     * @throws IOException Si ocurre un error al escribir la respuesta.
     * @throws ServletException Si ocurre un error en el procesamiento de la solicitud.
     */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ResponseEntity<Map<String, String>> errorResponseEntity;

		if (authException instanceof BadCredentialsException) {
			errorResponseEntity = responseUtil.error("error.unauthorizedAccess", HttpStatus.UNAUTHORIZED);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else if (authException instanceof AuthenticationException) {
			errorResponseEntity = responseUtil.error("error.forbiddenAccess", HttpStatus.FORBIDDEN);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} else {
			errorResponseEntity = responseUtil.error("error.unknownAccess", HttpStatus.FORBIDDEN);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}

		String json = objectMapper.writeValueAsString(errorResponseEntity.getBody());
		response.getWriter().write(json);
	}
}
