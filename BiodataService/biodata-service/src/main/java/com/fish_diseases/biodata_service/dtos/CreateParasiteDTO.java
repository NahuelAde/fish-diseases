package com.fish_diseases.biodata_service.dtos;

import java.util.List;

import com.fish_diseases.biodata_service.entities.Taxonomy;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para crear un nuevo registro de parásito.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParasiteDTO {

    /**
     * Nombre científico del parásito (obligatorio).
     */
	@NotBlank(message = "{IsRequired.scientificName}")
	private String scientificName;
	
    /**
     * Nombre común del parásito.
     */
	private String commonName;

    /**
     * Información taxonómica embebida.
     */
	private Taxonomy taxonomy;

    /**
     * Características diagnósticas (obligatorio).
     */
	@NotBlank(message = "{IsRequired.diagnosticFeatures}")
	private String diagnosticFeatures;
	
    /**
     * Tamaño estimado del parásito.
     */
	private String size;

    /**
     * Distribución geográfica conocida.
     */
	private String geographicalDistribution;

    /**
     * Detalles sobre su ciclo biológico.
     */
	private String biologicalCycle;

    /**
     * Especificidad hacia hospedadores.
     */
	private String hostSpecificity;

    /**
     * Lista de nombres científicos de peces hospedadores (obligatorio).
     */
	@NotEmpty(message = "{IsRequired.hosts}")
	private List<String> fishes;
	
    /**
     * Localización en el hospedador (obligatorio).
     */
	@NotEmpty(message = "{IsRequired.locationOnHost}")
	private String locationOnHost;
	
    /**
     * Daños causados al hospedador.
     */
	private String damagesOnHost;

    /**
     * Técnicas de detección utilizadas.
     */
	private String detection;
}
