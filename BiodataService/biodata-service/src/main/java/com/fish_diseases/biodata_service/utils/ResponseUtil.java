package com.fish_diseases.biodata_service.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Utilidad para construir respuestas HTTP personalizadas con mensajes internacionales.
 * 
 * Esta clase facilita la creación de respuestas de éxito o error, permitiendo incluir
 * mensajes personalizados basados en claves de un archivo de propiedades. Utiliza
 * el componente MessageSource para obtener los mensajes en el idioma adecuado.
 * 
 * Proporciona métodos para devolver respuestas estándar de éxito, error y validación.
 */
@Component
public class ResponseUtil {

	@Autowired
	private MessageSource messageSource;

    /**
     * Construye una respuesta de éxito con un mensaje personalizado.
     * 
     * @param messageKey Clave del mensaje a mostrar.
     * @param status Código de estado HTTP para la respuesta.
     * @return ResponseEntity con el mensaje de éxito y el código de estado.
     */
    public ResponseEntity<Map<String, String>> success(String messageKey, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("message", messageSource.getMessage(messageKey, null, Locale.getDefault()));
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Construye una respuesta de error general con un mensaje personalizado.
     * 
     * @param messageKey Clave del mensaje de error.
     * @param status Código de estado HTTP para la respuesta.
     * @return ResponseEntity con el mensaje de error y el código de estado.
     */
    public ResponseEntity<Map<String, String>> error(String messageKey, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("error", messageSource.getMessage(messageKey, null, Locale.getDefault()));
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Construye una respuesta de error por validación de campos con detalles específicos.
     * 
     * @param errors Mapa de errores de validación por campo.
     * @return ResponseEntity con el mapa de errores y el código de estado 400 (BAD_REQUEST).
     */
    public ResponseEntity<Map<String, List<String>>> validationError(Map<String, List<String>> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}