package com.fish_diseases.biodata_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase embebible que representa la información taxonómica de una especie biológica.
 * Incluye diferentes niveles jerárquicos de clasificación, desde el reino hasta la especie,
 * así como un identificador AphiaID y una URL de referencia.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taxonomy {

    /**
     * Identificador único asignado por el sistema WoRMS (AphiaID).
     */
    @Column(name = "aphiaid")
    private Integer aphiaID;

    /**
     * Reino al que pertenece la especie.
     */
    @Column(name = "kingdom")
    private String kingdom;

    /**
     * Filo o división taxonómica de la especie.
     */
    @Column(name = "phylum")
    private String phylum;

    /**
     * Clase taxonómica de la especie.
     */
    @Column(name = "class_name")
    private String className;

    /**
     * Orden taxonómico de la especie.
     */
    @Column(name = "order_name")
    private String orderName;

    /**
     * Familia taxonómica de la especie.
     */
    @Column(name = "family")
    private String family;

    /**
     * Género taxonómico de la especie.
     */
    @Column(name = "genus")
    private String genus;

    /**
     * Nombre de la especie.
     */
    @Column(name = "species")
    private String species;

    /**
     * URL de referencia para consultar la información taxonómica de la especie.
     */
    @Column(name = "taxonomy_url")
    private String url;
}
