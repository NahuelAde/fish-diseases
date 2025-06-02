package com.fish_diseases.treatment_service.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar los campos editables de un tratamiento existente.
 * Permite modificar la descripción, el espectro de acción y la dosis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTreatmentDTO {

    /**
     * Descripción del tratamiento. Debe contener al menos 5 caracteres.
     */
    @Size(min = 5, message = "{Size.treatment.description}")
    private String description;

    /**
     * Espectro de acción del tratamiento.
     */
    private String actionSpectrum;

    /**
     * Dosis recomendada del tratamiento.
     */
    private String dose;
}
