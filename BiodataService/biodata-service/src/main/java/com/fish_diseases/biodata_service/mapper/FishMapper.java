package com.fish_diseases.biodata_service.mapper;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.biodata_service.dtos.ApiFishDTO;
import com.fish_diseases.biodata_service.dtos.CreateFishDTO;
import com.fish_diseases.biodata_service.entities.Fish;
import com.fish_diseases.biodata_service.entities.Taxonomy;

/**
 * Mapper interface para convertir entre entidades {@link Fish} y los DTOs asociados.
 * Utiliza MapStruct para generar la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring", imports = {com.fish_diseases.biodata_service.entities.Parasite.class, java.util.stream.Collectors.class})
public interface FishMapper {

    /**
     * Convierte un {@link CreateFishDTO} a una entidad {@link Fish}.
     * 
     * @param createFishDTO El DTO de tipo {@link CreateFishDTO}.
     * @return La entidad {@link Fish} convertida.
     */
    @Mapping(source = "taxonomy", target = "taxonomy")
    @Mapping(target = "parasites", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Fish convertCreateFishDtoToFishEntity(CreateFishDTO createFishDTO);

    /**
     * Convierte un {@link ApiFishDTO} a una entidad {@link Fish}.
     * 
     * @param apiFishDTO El DTO de tipo {@link ApiFishDTO}.
     * @return La entidad {@link Fish} convertida.
     */
    @Mapping(source = "taxonomy", target = "taxonomy")
    @Mapping(target = "parasites", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Fish convertApiFishDtoToFishEntity(ApiFishDTO apiFishDTO);

    /**
     * Convierte una entidad {@link Fish} a un DTO {@link CreateFishDTO}.
     * 
     * @param entity La entidad {@link Fish}.
     * @return El DTO {@link CreateFishDTO} convertido.
     */
    @Mapping(source = "taxonomy", target = "taxonomy")
    @Mapping(target = "parasites", expression = "java(entity.getParasites().stream().map(Parasite::getScientificName).collect(java.util.stream.Collectors.toList()))")
    CreateFishDTO convertFishToCreateFishDto(Fish entity);

    /**
     * Convierte un {@link Map} de tipo {@link String} a un DTO {@link ApiFishDTO}.
     * 
     * @param fishData El mapa que contiene los datos a convertir.
     * @return El DTO {@link ApiFishDTO} generado a partir del mapa.
     */
    @Mapping(target = "taxonomy", expression = "java(fishData.get(\"taxonomy\") != null ? TaxonomyMapper.convertMapToApiTaxonomyDto((Map<String, Object>) fishData.get(\"taxonomy\")) : null)")
    ApiFishDTO convertMapToApiFishDto(Map<String, Object> fishData);

    /**
     * Convierte un objeto a su representación como {@link String}.
     * 
     * @param value El objeto a convertir.
     * @return La representación en {@link String} del objeto.
     */
    default String mapObjectToString(Object value) {
        return value != null ? value.toString() : null;
    }
    
    /**
     * Convierte un objeto {@link Map} a {@link Taxonomy} si es posible.
     * 
     * @param taxonomyData Los datos del mapa para convertir.
     * @return La entidad {@link Taxonomy} convertida, o null si los datos no son del tipo adecuado.
     */
    @Mapping(target = "taxonomy", expression = "java(convertTaxonomy(fishData.get(\"taxonomy\")))")
    @SuppressWarnings("unchecked")
    default Taxonomy convertTaxonomy(Object taxonomyData) {
        if (taxonomyData instanceof Map) {
            return TaxonomyMapper.convertMapToTaxonomy((Map<String, Object>) taxonomyData);
        }
        return null;
    }
}
