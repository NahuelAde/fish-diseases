package com.fish_diseases.auth_service.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fish_diseases.auth_service.exceptions.RoleNotFoundException;
import com.fish_diseases.auth_service.exceptions.UserNotFoundException;
import com.fish_diseases.auth_service.utils.ResponseUtil;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Manejador global de excepciones para la aplicación. 
 * Esta clase se encarga de gestionar las excepciones que ocurren en los controladores
 * y devolver respuestas adecuadas en formato JSON con el código de estado HTTP correspondiente.
 * 
 * Utiliza el servicio {@link ResponseUtil} para generar respuestas de error personalizadas.
 * Maneja errores de validación de entrada, usuarios no encontrados, tokens expirados, 
 * entre otros.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ResponseUtil responseUtil;

	/**
	 * Maneja las excepciones de tipo {@link UserNotFoundException}.
	 * Devuelve una respuesta con el mensaje de error "error.userNotFound" y el estado HTTP 404.
	 *
	 * @return La respuesta con el error y el estado HTTP NOT_FOUND.
	 */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException() {
		return responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones de tipo {@link RoleNotFoundException}.
     * Devuelve una respuesta con el mensaje de error "error.roleNotFound" y el estado HTTP 404.
     *
     * @return La respuesta con el error y el estado HTTP NOT_FOUND.
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRoleNotFoundException() {
		return responseUtil.error("error.roleNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones de tipo {@link UsernameNotFoundException}.
     * Devuelve una respuesta con el mensaje de error "error.usernameNotFound" y el estado HTTP 404.
     * 
     * @return La respuesta con el error y el estado HTTP NOT_FOUND.
     */
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUsernameNotFound() {
		return responseUtil.error("error.usernameNotFound", HttpStatus.NOT_FOUND);
	}

    /**
     * Maneja las excepciones de tipo {@link ExpiredJwtException}.
     * Devuelve una respuesta con el mensaje de error "error.tokenExpired" y el estado HTTP 401.
     * 
     * @return La respuesta con el error y el estado HTTP UNAUTHORIZED.
     */
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Map<String, String>> handleExpiredToken() {
		return responseUtil.error("error.tokenExpired", HttpStatus.UNAUTHORIZED);
	}

    /**
     * Maneja las excepciones de tipo {@link MethodArgumentNotValidException}.
     * Esta excepción se lanza cuando la validación de los argumentos de un método falla.
     * Devuelve una respuesta con los detalles de los errores de validación en formato JSON y el 
     * estado HTTP 400.
     * 
     * @param ex La excepción que contiene los errores de validación.
     * @return La respuesta con los errores de validación y el estado HTTP BAD_REQUEST.
     */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, List<String>> errors = new HashMap<>();

		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.computeIfAbsent(error.getField(), k -> new ArrayList<>());
		}

		return responseUtil.validationError(errors);
	}

}
