package com.fish_diseases.biodata_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un parásito en la estructura esperada desde una API externa.
 * Incluye datos taxonómicos, características diagnósticas y relaciones con hospedadores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiParasiteDTO {

    /**
     * Nombre científico del parásito.
     */
    private String scientificName;

    /**
     * Nombre común del parásito.
     */
    private String commonName;

    /**
     * Información taxonómica asociada al parásito.
     */
    private ApiTaxonomyDTO taxonomy;

    /**
     * Características diagnósticas que permiten identificar el parásito.
     */
    private String diagnosticFeatures;

    /**
     * Tamaño del parásito.
     */
    private String size;

    /**
     * Distribución geográfica conocida del parásito.
     */
    private String geographicalDistribution;

    /**
     * Ciclo biológico del parásito.
     */
    private String biologicalCycle;

    /**
     * Especificidad hacia ciertos hospedadores.
     */
    private String hostSpecificity;

    /**
     * Localización del parásito en el cuerpo del hospedador.
     */
    private String locationOnHost;

    /**
     * Daños que el parásito causa en el hospedador.
     */
    private String damagesOnHost;

    /**
     * Métodos utilizados para la detección del parásito.
     */
	private String detection;
}