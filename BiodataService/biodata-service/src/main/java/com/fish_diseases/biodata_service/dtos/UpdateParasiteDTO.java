package com.fish_diseases.biodata_service.dtos;

import java.util.List;

import com.fish_diseases.biodata_service.entities.Taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar los datos de un parásito existente.
 * Permite modificar campos específicos sin necesidad de recrear la entidad completa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateParasiteDTO {

    /**
     * Nombre común del parásito.
     */
	private String commonName;

    /**
     * Información taxonómica embebida.
     */
	private Taxonomy taxonomy;

    /**
     * Características diagnósticas del parásito.
     */
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
     * Grado de especificidad hacia hospedadores.
     */
	private String hostSpecificity;

    /**
     * Lista de nombres científicos de peces hospedadores.
     */
	private List<String> fishes;

    /**
     * Localización del parásito en el cuerpo del hospedador.
     */
	private String locationOnHost;

    /**
     * Daños que el parásito causa en el hospedador.
     */
	private String damagesOnHost;

    /**
     * Métodos para detectar el parásito.
     */
	private String detection;
}
