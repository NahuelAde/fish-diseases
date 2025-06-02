package com.fish_diseases.treatment_service.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar la técnica asociada a un método de laboratorio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLaboratoryMethodDTO {

    /**
     * Técnica de laboratorio. Debe contener al menos 5 caracteres.
     */
    @Size(min = 5, message = "{Size.laboratoryMethod.technique}")
	private String technique;

}
