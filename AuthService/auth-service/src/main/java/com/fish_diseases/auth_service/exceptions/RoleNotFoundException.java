package com.fish_diseases.auth_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra un rol en el sistema.
 * 
 * Se lanza típicamente cuando se intenta acceder o asignar un rol que no existe 
 * en la base de datos. Esta excepción retorna un código de estado HTTP 404 (NOT FOUND).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RoleNotFoundException() {
	}

}
