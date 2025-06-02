package com.fish_diseases.treatment_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fish_diseases.treatment_service.dtos.CreateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.dtos.UpdateLaboratoryMethodDTO;
import com.fish_diseases.treatment_service.entities.LaboratoryMethod;

/**
 * Mapper de MapStruct para convertir entre DTOs y entidades de {@link LaboratoryMethod}.
 * Define las reglas de mapeo necesarias para crear o actualizar métodos de laboratorio.
 */
@Mapper(componentModel = "spring")
public interface LaboratoryMethodMapper {

    /**
     * Convierte un {@link CreateLaboratoryMethodDTO} a una entidad {@link LaboratoryMethod}.
     * Ignora campos como ID y timestamps que son gestionados automáticamente.
     *
     * @param createLaboratoryMethodDTO DTO con los datos para crear.
     * @return una nueva instancia de LaboratoryMethod lista para persistencia.
     */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	LaboratoryMethod convertCreateLaboratoryMethodDtoToLaboratoryMethodEntity(CreateLaboratoryMethodDTO createLaboratoryMethodDTO);

    /**
     * Convierte un {@link UpdateLaboratoryMethodDTO} a una entidad {@link LaboratoryMethod},
     * ignorando campos que no deben modificarse directamente como nombre o timestamps.
     *
     * @param updateLaboratoryMethodDTO DTO con los datos para actualización.
     * @return entidad parcialmente actualizada.
     */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	LaboratoryMethod convertUpdateLaboratoryMethodDtoToLaboratoryMethodEntity(UpdateLaboratoryMethodDTO updateLaboratoryMethodDTO);
}
