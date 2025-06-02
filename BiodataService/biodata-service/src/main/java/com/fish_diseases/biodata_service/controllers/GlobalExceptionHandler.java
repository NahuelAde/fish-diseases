package com.fish_diseases.biodata_service.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fish_diseases.biodata_service.exceptions.FishNotFoundException;
import com.fish_diseases.biodata_service.exceptions.ParasiteNotFoundException;
import com.fish_diseases.biodata_service.utils.ResponseUtil;

/**
 * Manejador global de excepciones para el servicio de datos biológicos.
 * Esta clase gestiona las excepciones lanzadas por los controladores relacionados con peces y parásitos,
 * devolviendo respuestas JSON con los códigos de estado HTTP apropiados.
 * 
 * Utiliza el componente {@link ResponseUtil} para generar respuestas de error estandarizadas.
 * Maneja errores como entidades no encontradas, violaciones de integridad de datos, argumentos inválidos 
 * y errores generales.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ResponseUtil responseUtil;

    /**
     * Maneja excepciones de tipo {@link ParasiteNotFoundException}.
     * Devuelve un mensaje de error con código 404 (NO_FOUND).
     */
    @ExceptionHandler(ParasiteNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleParasiteNotFoundException() {
		return responseUtil.error("error.parasiteNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo {@link FishNotFoundException}.
     * Devuelve un mensaje de error con código 404 (NOT_FOUND).
     */
    @ExceptionHandler(FishNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFishNotFoundException() {
		return responseUtil.error("error.fishNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja violaciones de integridad de datos (como claves duplicadas o restricciones únicas).
     * Devuelve un mensaje de error con código 409 (CONFLICT).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation() {
        return responseUtil.error("error.dataIntegrityViolation", HttpStatus.CONFLICT);
    }
    
    /**
     * Maneja excepciones causadas por argumentos ilegales (por ejemplo, IDs con formato inválido).
     * Devuelve un mensaje de error con código 400 (BAD_REQUEST).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return responseUtil.error("error.invalidIdFormat", HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción no controlada.
     * Devuelve un mensaje de error con código 500 (INTERNAL_SERVER_ERROR).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return responseUtil.error("error.generalError", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja errores de validación en los argumentos de los métodos del controlador.
     * Devuelve un mapa con los errores por campo y código 400 (BAD_REQUEST).
     *
     * @param ex excepción que contiene los errores de validación.
     * @return respuesta con detalles de los campos inválidos.
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
