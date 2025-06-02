package com.fish_diseases.biodata_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un pez tal como es recibido desde una API externa.
 * Contiene información básica y taxonómica del pez.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiFishDTO {

    /**
     * Nombre científico del pez.
     */
    private String scientificName;

    /**
     * Nombre común del pez.
     */
    private String commonName;

    /**
     * Información taxonómica asociada al pez.
     */
    private ApiTaxonomyDTO taxonomy;

    /**
     * Distribución geográfica del pez según clasificación de la FAO.
     */
    private String faoDistribution;
}