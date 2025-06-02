package com.fish_diseases.treatment_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para la creación de un nuevo tratamiento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreatmentDTO {

    /**
     * Nombre del tratamiento. Campo obligatorio con longitud entre 2 y 50 caracteres.
     */
	@NotBlank(message = "{IsRequired.treatment.name}")
    @Size(min = 2, max = 50, message = "{Size.treatment.name}")
	private String name;

    /**
     * Descripción del tratamiento. Campo obligatorio con mínimo 5 caracteres.
     */
	@NotBlank(message = "{IsRequired.treatment.description}")
    @Size(min = 5, message = "{Size.treatment.description}")
	private String description;

    /**
     * Espectro de acción del tratamiento.
     */
    private String actionSpectrum;

    /**
     * Dosis recomendada para el tratamiento.
     */
	private String dose;
}
