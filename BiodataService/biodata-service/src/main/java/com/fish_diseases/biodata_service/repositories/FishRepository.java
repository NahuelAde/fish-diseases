package com.fish_diseases.biodata_service.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fish_diseases.biodata_service.entities.Fish;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la entidad {@link Fish}, proporciona operaciones CRUD
 * y consultas específicas sobre peces.
 */
@Repository
@Transactional
public interface FishRepository extends JpaRepository<Fish, Long> {

    /**
     * Busca un pez por su nombre científico.
     *
     * @param scientificName Nombre científico del pez.
     * @return {@link Optional} conteniendo el pez si se encuentra.
     */
    Optional<Fish> findByScientificName(String scientificName);

    /**
     * Busca todos los peces cuyos nombres científicos estén en la lista proporcionada.
     *
     * @param scientificNames Lista de nombres científicos.
     * @return Lista de peces coincidentes.
     */
    List<Fish> findByScientificNameIn(List<String> scientificNames);
    
    /**
     * Actualiza la fecha de modificación de un pez.
     *
     * @param id         ID del pez.
     * @param updatedAt  Nueva fecha de modificación.
     */
	@Modifying
	@Query("UPDATE Fish f SET f.updatedAt = :updatedAt WHERE f.id = :id")
	void updateUpdatedAt(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);

}
