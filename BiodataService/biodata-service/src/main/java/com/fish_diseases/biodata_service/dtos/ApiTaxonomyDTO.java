package com.fish_diseases.biodata_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la información taxonómica obtenida desde una API externa,
 * como WoRMS (World Register of Marine Species). Mapea directamente campos relevantes del JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTaxonomyDTO {

    /**
     * Identificador único del taxón según WoRMS.
     */
    @JsonProperty("AphiaID")
    private Integer aphiaID;

    /**
     * Nombre científico completo del organismo.
     */
    @JsonProperty("scientificname")
    private String scientificName;

    /**
     * Reino taxonómico del organismo.
     */
    private String kingdom;

    /**
     * Filo taxonómico del organismo.
     */
    private String phylum;

    /**
     * Clase taxonómica del organismo.
     */
    @JsonProperty("class")
    private String className;

    /**
     * Orden taxonómico del organismo.
     */
    @JsonProperty("order")
    private String orderName;

    /**
     * Familia taxonómica del organismo.
     */
    private String family;

    /**
     * Género taxonómico del organismo.
     */
    private String genus;

    /**
     * URL con más información sobre el taxón.
     */
    @JsonProperty("url")
    private String url;
}