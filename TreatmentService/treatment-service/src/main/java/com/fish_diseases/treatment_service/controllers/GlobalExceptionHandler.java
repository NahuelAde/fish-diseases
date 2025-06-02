package com.fish_diseases.treatment_service.controllers;

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

import com.fish_diseases.treatment_service.exceptions.LaboratoryMethodNotFoundException;
import com.fish_diseases.treatment_service.exceptions.TreatmentNotFoundException;
import com.fish_diseases.treatment_service.utils.ResponseUtil;

/**
 * Manejador global de excepciones para el servicio de tratamientos.
 * Se encarga de capturar las excepciones producidas por los controladores que gestionan
 * tratamientos y métodos de laboratorio, devolviendo respuestas en formato JSON con códigos
 * HTTP adecuados.
 * 
 * Utiliza el servicio {@link ResponseUtil} para construir las respuestas de error personalizadas.
 * Controla errores como entidades no encontradas, errores de validación, conflictos de datos
 * y otros fallos genéricos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ResponseUtil responseUtil;

    /**
     * Maneja excepciones de tipo {@link TreatmentNotFoundException}.
     * Devuelve un mensaje de error con código 404 (NOT_FOUND).
     */
    @ExceptionHandler(TreatmentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleTreatmentNotFoundException() {
		return responseUtil.error("error.treatmentNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo {@link LaboratoryMethodNotFoundException}.
     * Devuelve un mensaje de error con código 404 (NOT_FOUND).
     */
    @ExceptionHandler(LaboratoryMethodNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLaboratoryMethodNotFoundException() {
		return responseUtil.error("error.laboratoryMethodNotFound", HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja violaciones de integridad de datos (por ejemplo, intentos de insertar registros duplicados).
     * Devuelve un mensaje de error con código 409 (CONFLICT).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation() {
        return responseUtil.error("error.dataIntegrityViolation", HttpStatus.CONFLICT);
    }
    
    /**
     * Maneja argumentos no válidos (como identificadores con formato incorrecto).
     * Devuelve un mensaje de error con código 400 (BAD_REQUEST).
     */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException() {
		return responseUtil.error("error.invalidIdFormat", HttpStatus.BAD_REQUEST);
	}

    /**
     * Maneja cualquier excepción genérica no prevista.
     * Devuelve un mensaje de error con código 500 (INTERNAL_SERVER_ERROR).
     */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
		ex.printStackTrace();
		return responseUtil.error("error.generalError", HttpStatus.INTERNAL_SERVER_ERROR);
	}

    /**
     * Maneja errores de validación de argumentos en peticiones.
     * Devuelve los errores de cada campo con código 400 (BAD_REQUEST).
     *
     * @param ex excepción que contiene los errores de validación.
     * @return respuesta con los errores agrupados por campo.
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
