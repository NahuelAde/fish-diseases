package com.fish_diseases.biodata_service.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Utilidad para manejar errores de validación en las solicitudes.
 * 
 * Esta clase ofrece un método para construir un mapa de errores de validación basado
 * en los resultados del proceso de validación (BindingResult), que agrupa los errores por campo.
 * El mapa resultante se utiliza para generar una respuesta personalizada con detalles
 * de los errores de validación.
 */
public class ValidationUtils {

    /**
     * Construye una respuesta con los errores de validación de un BindingResult.
     * 
     * @param result Resultado de la validación, contiene los errores de los campos.
     * @param responseUtil Utilidad para crear las respuestas de error.
     * @return ResponseEntity con los errores de validación.
     */
    public static ResponseEntity<?> buildValidationError(BindingResult result, ResponseUtil responseUtil) {
        Map<String, List<String>> errors = result.getFieldErrors().stream()
            .collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            ));

        return responseUtil.validationError(errors);
    }

}
