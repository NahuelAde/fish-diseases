package com.fish_diseases.auth_service.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fish_diseases.auth_service.entities.Role;
import com.fish_diseases.auth_service.entities.User;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la entidad {@link User}, que proporciona operaciones CRUD y consultas personalizadas
 * sobre los usuarios en la base de datos.
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * Comprueba si existe un usuario con el DNI especificado.
     *
     * @param nationalId DNI del usuario.
     * @return {@code true} si existe un usuario con ese DNI, {@code false} en caso contrario.
     */
	boolean existsByNationalId(String nationalId);
	
    /**
     * Comprueba si existe un usuario con el nombre de usuario especificado.
     *
     * @param username Nombre de usuario.
     * @return {@code true} si el nombre de usuario ya existe, {@code false} en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Comprueba si existe un usuario con el correo electrónico especificado.
     *
     * @param email Correo electrónico del usuario.
     * @return {@code true} si el correo ya existe, {@code false} en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return {@link Optional} conteniendo el usuario si existe.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
	
    /**
     * Devuelve una lista de usuarios que tienen el rol especificado.
     *
     * @param roleName Rol del usuario.
     * @return Lista de usuarios con el rol dado.
     */
	List<User> findByRoles_Name(Role roleName);
	
    /**
     * Devuelve todos los usuarios que están habilitados.
     *
     * @return Lista de usuarios activos.
     */
	List<User> findAllByEnabledTrue();
    
    /**
     * Actualiza la fecha del último inicio de sesión de un usuario.
     *
     * @param userId     ID del usuario.
     * @param lastLogin  Fecha y hora del último inicio de sesión.
     */
	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.userId = :userId")
	void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * Actualiza la fecha de modificación de un usuario.
     *
     * @param userId     ID del usuario.
     * @param updatedAt  Nueva fecha de modificación.
     */
	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.updatedAt = :updatedAt WHERE u.userId = :userId")
	void updateUpdatedAt(@Param("userId") Long userId, @Param("updatedAt") LocalDateTime updatedAt);
	
    /**
     * Actualiza el estado de habilitación de un usuario.
     *
     * @param userId  ID del usuario.
     * @param enabled Nuevo estado del atributo enabled.
     */
	@Modifying
	@Query("UPDATE User u SET u.enabled = :enabled WHERE u.userId = :userId")
	void updateEnabledStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);
}
