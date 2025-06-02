package com.fish_diseases.biodata_service.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.biodata_service.dtos.ApiParasiteDTO;
import com.fish_diseases.biodata_service.dtos.CreateParasiteDTO;
import com.fish_diseases.biodata_service.entities.Parasite;
import com.fish_diseases.biodata_service.entities.Taxonomy;

/**
 * Mapper interface para convertir entre entidades {@link Parasite} y los DTOs asociados.
 * Utiliza MapStruct para generar la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring", uses = TaxonomyMapper.class, imports = {com.fish_diseases.biodata_service.entities.Fish.class, java.util.stream.Collectors.class})
public interface ParasiteMapper {

    /**
     * Convierte un {@link ApiParasiteDTO} a una entidad {@link Parasite}.
     * 
     * @param apiParasiteDTO El DTO de tipo {@link ApiParasiteDTO}.
     * @return La entidad {@link Parasite} convertida.
     */
	@Mapping(source = "taxonomy", target = "taxonomy")
	@Mapping(target = "fishes", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Parasite convertApiParasiteDtoToParasiteEntity(ApiParasiteDTO apiParasiteDTO);

    /**
     * Convierte un {@link CreateParasiteDTO} a una entidad {@link Parasite}.
     * 
     * @param createParasiteDTO El DTO de tipo {@link CreateParasiteDTO}.
     * @return La entidad {@link Parasite} convertida.
     */
	@Mapping(source = "taxonomy", target = "taxonomy")
	@Mapping(target = "fishes", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Parasite convertCreateParasiteDtoToParasiteEntity(CreateParasiteDTO createParasiteDTO);

    /**
     * Convierte una entidad {@link Parasite} a un DTO {@link CreateParasiteDTO}.
     * 
     * @param entity La entidad {@link Parasite}.
     * @return El DTO {@link CreateParasiteDTO} convertido.
     */
	@Mapping(source = "taxonomy", target = "taxonomy")
	@Mapping(target = "fishes", expression = "java(entity.getFishes().stream().map(Fish::getScientificName).collect(java.util.stream.Collectors.toList()))")
	CreateParasiteDTO convertParasiteToCreateParasiteDto(Parasite entity);

    /**
     * Convierte un {@link Map} de tipo {@link String} a un DTO {@link ApiParasiteDTO}.
     * 
     * @param parasiteData El mapa que contiene los datos a convertir.
     * @return El DTO {@link ApiParasiteDTO} generado a partir del mapa.
     */
	@Mapping(target = "taxonomy", expression = "java(parasiteData.get(\"taxonomy\") != null ? TaxonomyMapper.convertMapToApiTaxonomyDto((Map<String, Object>) parasiteData.get(\"taxonomy\")) : null)")
	ApiParasiteDTO convertMapToApiParasiteDto(Map<String, Object> parasiteData);

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
     * @return La entidad {@link Taxonomy} convertida.
     */
	@Mapping(target = "taxonomy", expression = "java(convertTaxonomy(parasiteData.get(\"taxonomy\")))")
	@SuppressWarnings("unchecked")
	default Taxonomy convertTaxonomy(Object taxonomyData) {
		if (taxonomyData instanceof Map) {
			return TaxonomyMapper.convertMapToTaxonomy((Map<String, Object>) taxonomyData);
		}
		return null;
	}

}
