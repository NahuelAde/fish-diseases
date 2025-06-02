package com.fish_diseases.biodata_service.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fish_diseases.biodata_service.entities.Parasite;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la entidad {@link Parasite}, encargado de operaciones CRUD
 * y consultas específicas sobre parásitos.
 */
@Repository
@Transactional
public interface ParasiteRepository extends JpaRepository<Parasite, Long> {

    /**
     * Busca un parásito por su nombre científico.
     *
     * @param scientificName Nombre científico del parásito.
     * @return {@link Optional} conteniendo el parásito si existe.
     */
    Optional<Parasite> findByScientificName(String scientificName);

    /**
     * Busca todos los parásitos cuyos nombres científicos estén en la lista proporcionada.
     *
     * @param scientificNames Lista de nombres científicos.
     * @return Lista de parásitos coincidentes.
     */
    List<Parasite> findByScientificNameIn(List<String> scientificNames);
    
    /**
     * Actualiza la fecha de modificación de un parásito.
     *
     * @param id         ID del parásito.
     * @param updatedAt  Nueva fecha de modificación.
     */
	@Modifying
	@Query("UPDATE Parasite p SET p.updatedAt = :updatedAt WHERE p.id = :id")
	void updateUpdatedAt(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
