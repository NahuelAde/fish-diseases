package com.fish_diseases.treatment_service.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fish_diseases.treatment_service.entities.LaboratoryMethod;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la entidad {@link LaboratoryMethod}, utilizado para acceder y modificar
 * los métodos de laboratorio en el sistema.
 */
@Repository
@Transactional
public interface LaboratoryMethodRepository extends JpaRepository<LaboratoryMethod, Integer> {

    /**
     * Busca un método de laboratorio por su nombre.
     *
     * @param laboratoryMethodName Nombre del método.
     * @return {@link Optional} conteniendo el método si se encuentra.
     */
	Optional<LaboratoryMethod> findByName(String laboratoryMethodName);
	
    /**
     * Actualiza la fecha de modificación de un método de laboratorio.
     *
     * @param id         ID del método.
     * @param updatedAt  Nueva fecha de modificación.
     */
	@Modifying
	@Query("UPDATE LaboratoryMethod l SET l.updatedAt = :updatedAt WHERE l.id = :id")
	void updateUpdatedAt(@Param("id") int id, @Param("updatedAt") LocalDateTime updatedAt);
	
}
