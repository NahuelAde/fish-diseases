package com.fish_diseases.biodata_service.dtos;

import java.util.List;

import com.fish_diseases.biodata_service.entities.Taxonomy;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para crear un nuevo pez en el sistema.
 * Incluye su información taxonómica, distribución y parásitos asociados.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFishDTO {

    /**
     * Nombre científico del pez (obligatorio).
     */
	@NotBlank(message = "{IsRequired.scientificName}")
    private String scientificName;
	
    /**
     * Nombre común del pez.
     */
    private String commonName;

    /**
     * Información taxonómica embebida.
     */
    private Taxonomy taxonomy;

    /**
     * Distribución FAO.
     */
    private String faoDistribution;

    /**
     * Lista de nombres científicos de parásitos asociados.
     */
	private List<String> parasites;
}
