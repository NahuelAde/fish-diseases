package com.fish_diseases.biodata_service.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.biodata_service.dtos.ApiTaxonomyDTO;
import com.fish_diseases.biodata_service.entities.Taxonomy;

/**
 * Mapper interface para convertir entre entidades {@link Taxonomy} y los DTOs asociados.
 * Utiliza MapStruct para generar la implementación en tiempo de compilación.
 */
@Mapper(componentModel = "spring")
public interface TaxonomyMapper {

    /**
     * Mapea un objeto {@link Taxonomy} a otro objeto de tipo {@link Taxonomy}.
     * Este es un mapeo directo.
     * 
     * @param taxonomy Objeto de tipo {@link Taxonomy} a mapear.
     * @return El objeto {@link Taxonomy} mapeado.
     */
	Taxonomy map(Taxonomy taxonomy);
	
    /**
     * Convierte un {@link ApiTaxonomyDTO} a una entidad {@link Taxonomy}.
     * Mapea el campo "scientificName" a "species".
     * 
     * @param dto El DTO de tipo {@link ApiTaxonomyDTO}.
     * @return La entidad {@link Taxonomy} convertida.
     */
	@Mapping(source = "scientificName", target = "species")
	Taxonomy convertApiTaxonomyDtoToTaxonomy(ApiTaxonomyDTO dto);

    /**
     * Convierte una entidad {@link Taxonomy} a un {@link ApiTaxonomyDTO}.
     * Mapea el campo "species" a "scientificName".
     * 
     * @param taxonomy La entidad {@link Taxonomy}.
     * @return El DTO de tipo {@link ApiTaxonomyDTO} convertido.
     */
	@Mapping(source = "species", target = "scientificName")
	ApiTaxonomyDTO convertTaxonomyToApiTaxonomyDTO(Taxonomy taxonomy);

    /**
     * Convierte un {@link Map} de tipo {@link String} a {@link Taxonomy}.
     * Este mapeo es manual, ya que MapStruct no lo soporta de forma nativa.
     * 
     * @param data El mapa que contiene los datos a convertir.
     * @return La entidad {@link Taxonomy} generada a partir del mapa.
     */
	static Taxonomy convertMapToTaxonomy(Map<String, Object> data) {
		return Taxonomy
				.builder()
				.kingdom((String) data.get("kingdom"))
				.phylum((String) data.get("phylum"))
				.className((String) data.get("class"))
				.orderName((String) data.get("order"))
				.family((String) data.get("family"))
				.genus((String) data.get("genus"))
				.species((String) data.get("scientificname"))
				.build();
	}

    /**
     * Convierte un {@link Map} de tipo {@link String} a {@link ApiTaxonomyDTO}.
     * Este mapeo es manual.
     * 
     * @param data El mapa que contiene los datos a convertir.
     * @return El {@link ApiTaxonomyDTO} generado a partir del mapa.
     */
	static ApiTaxonomyDTO convertMapToApiTaxonomyDto(Map<String, Object> data) {
		return ApiTaxonomyDTO
				.builder()
				.aphiaID((Integer) data.get("AphiaID"))
				.scientificName((String) data.get("scientificname"))
				.kingdom((String) data.get("kingdom"))
				.phylum((String) data.get("phylum"))
				.className((String) data.get("class"))
				.orderName((String) data.get("order"))
				.family((String) data.get("family"))
				.genus((String) data.get("genus"))
				.url((String) data.get("url"))
				.build();
	}

    /**
     * Convierte un {@link ApiTaxonomyDTO} a un {@link Map}.
     * 
     * @param dto El {@link ApiTaxonomyDTO} que se convertirá a un mapa.
     * @return El mapa que contiene los datos de {@link ApiTaxonomyDTO}.
     */
	default Map<String, Object> convertApiTaxonomyDtoToMap(ApiTaxonomyDTO dto) {
		if (dto == null) {
			return null;
		}
		Map<String, Object> taxonomyMap = Map
				.of(
						"aphiaID", dto.getAphiaID(), 
						"scientificName", dto.getScientificName(),
						"kingdom", dto.getKingdom(), 
						"phylum", dto.getPhylum(), 
						"class_name", dto.getClassName(), 
						"order_name", dto.getOrderName(), 
						"family", dto.getFamily(), 
						"genus", dto.getGenus());

		return Map.of("scientificName", dto.getScientificName(), "taxonomy", taxonomyMap);
	}
}
