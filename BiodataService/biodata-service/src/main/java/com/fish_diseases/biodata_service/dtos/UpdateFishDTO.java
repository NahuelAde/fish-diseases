package com.fish_diseases.biodata_service.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.fish_diseases.biodata_service.entities.Taxonomy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar información de un pez existente.
 * Permite cambios parciales como nombre común, taxonomía o distribución.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFishDTO {

    /**
     * Nombre común del pez.
     */
    private String commonName;

    /**
     * Información taxonómica embebida.
     */
    private Taxonomy taxonomy;

    /**
     * Distribución geográfica según clasificación de la FAO.
     */
    private String faoDistribution;

    /**
     * Lista de nombres científicos de parásitos asociados.
     */
	private List<String> parasites;

    /**
     * Marca de tiempo de la última modificación.
     */
    private LocalDateTime lastUpdate;
    
}
