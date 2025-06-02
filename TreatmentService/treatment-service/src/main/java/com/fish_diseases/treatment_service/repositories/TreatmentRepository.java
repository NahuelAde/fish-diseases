package com.fish_diseases.treatment_service.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fish_diseases.treatment_service.entities.Treatment;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la entidad {@link Treatment}, encargado de la gestión
 * de tratamientos registrados en el sistema.
 */
@Repository
@Transactional
public interface TreatmentRepository extends JpaRepository<Treatment, Integer> {

    /**
     * Busca un tratamiento por su nombre.
     *
     * @param treatmentName Nombre del tratamiento.
     * @return {@link Optional} conteniendo el tratamiento si existe.
     */
	Optional<Treatment> findByName(String treatmentName);
	
    /**
     * Actualiza la fecha de modificación de un tratamiento.
     *
     * @param id         ID del tratamiento.
     * @param updatedAt  Nueva fecha de modificación.
     */
	@Modifying
	@Query("UPDATE Treatment t SET t.updatedAt = :updatedAt WHERE t.id = :id")
	void updateUpdatedAt(@Param("id") int id, @Param("updatedAt") LocalDateTime updatedAt);
	
}
