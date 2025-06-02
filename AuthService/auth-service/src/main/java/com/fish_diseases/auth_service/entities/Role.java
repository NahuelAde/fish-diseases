package com.fish_diseases.auth_service.entities;

import java.util.List;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un rol dentro del sistema. Define los diferentes tipos
 * de roles que un usuario puede tener. Esta clase también gestiona la relación
 * con la entidad `User` mediante una relación Many-to-Many.
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

	/**
	 * Enum que define los tipos de roles disponibles en el sistema.
	 */
	public enum RoleType {

		ROLE_TREATMENT,
		ROLE_ADMIN
	}

	/**
	 * Identificador único del rol.
	 */
	@Id
	@Column(name = "role_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int roleId;

	/**
	 * Nombre del rol.
	 */
	@NaturalId
	@Enumerated(EnumType.STRING)
	@Column(unique = true, nullable = false, length = 20)
	private RoleType name;

	/**
	 * Usuarios asociados a este rol.
	 */
	@JsonIgnoreProperties({ "roles", "handler", "hibernateLazyInitializer" })
	@ManyToMany(mappedBy = "roles")
	private List<User> users;

	/**
	 * Constructor por parámetros
	 * 
	 * @param name El tipo de rol a asignar al nuevo objeto Role.
	 */
	public Role(RoleType name) {
		this.name = name;
	}

	/**
	 * Constructor para usar una cadena y convertirla a RoleType.
	 * 
	 * @param roleName El nombre del rol como una cadena de texto (String).
	 */
	public Role(String roleName) {
		this.name = RoleType.valueOf(roleName);
	}

}
