package com.fish_diseases.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fish_diseases.auth_service.entities.Role;

/**
 * Repositorio para la entidad {@link Role}, que proporciona operaciones de acceso
 * a los datos de los roles en la base de datos.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
	
    /**
     * Busca un rol por su nombre.
     *
     * @param name Tipo de rol.
     * @return {@link Optional} conteniendo el rol si se encuentra.
     */
	Optional<Role> findByName(Role.RoleType name);

}
