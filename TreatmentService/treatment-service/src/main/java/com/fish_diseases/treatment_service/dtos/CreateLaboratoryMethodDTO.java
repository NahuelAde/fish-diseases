package com.fish_diseases.treatment_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para la creación de un nuevo método de laboratorio.
 * Requiere un nombre y una descripción técnica de la metodología.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLaboratoryMethodDTO {

    /**
     * Nombre del método de laboratorio. Campo obligatorio con longitud entre 2 y 50 caracteres.
     */
	@NotBlank(message = "{IsRequired.laboratoryMethod.name}")
    @Size(min = 2, max = 50, message = "{Size.laboratoryMethod.name}")
	private String name;

    /**
     * Técnica utilizada en el método de laboratorio. Campo obligatorio con mínimo 5 caracteres.
     */
	@NotBlank(message = "{IsRequired.laboratoryMethod.technique}")
    @Size(min = 5, message = "{Size.laboratoryMethod.technique}")
	private String technique;

}
