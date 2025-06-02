package com.fish_diseases.treatment_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.treatment_service.dtos.CreateTreatmentDTO;
import com.fish_diseases.treatment_service.dtos.UpdateTreatmentDTO;
import com.fish_diseases.treatment_service.entities.Treatment;

/**
 * Mapper de MapStruct para convertir entre DTOs y entidades de {@link Treatment}.
 * Facilita la conversión desde y hacia objetos de negocio para operaciones de persistencia.
 */
@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    /**
     * Convierte un {@link CreateTreatmentDTO} a una entidad {@link Treatment},
     * ignorando campos generados automáticamente como ID y fechas.
     *
     * @param createTreatmentDTO DTO con la información del tratamiento a crear.
     * @return entidad Treatment construida a partir del DTO.
     */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Treatment convertCreateTreatmentDtoToTreatmentEntity(CreateTreatmentDTO createTreatmentDTO);

    /**
     * Convierte un {@link UpdateTreatmentDTO} a una entidad {@link Treatment},
     * excluyendo campos inmodificables como el nombre o las fechas.
     *
     * @param updateTreatmentDTO DTO con la información actualizada del tratamiento.
     * @return entidad Treatment parcialmente actualizada.
     */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Treatment convertUpdateTreatmentDtoToTreatmentEntity(UpdateTreatmentDTO updateTreatmentDTO);
}
