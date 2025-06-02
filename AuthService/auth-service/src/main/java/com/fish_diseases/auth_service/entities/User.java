package com.fish_diseases.auth_service.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fish_diseases.auth_service.entities.Role.RoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un usuario dentro del sistema. Contiene los
 * atributos relacionados con la información del usuario, como nombre, usuario,
 * contraseña, roles, etc. Además, incluye métodos para gestionar los roles y su
 * representación.
 * 
 * Las relaciones entre los usuarios y los roles se gestionan mediante una
 * relación Many-to-Many.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	/**
	 * Identificador único del usuario.
	 */
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	/**
	 * Nombre del usuario.
	 */
	private String firstname;

	/**
	 * Apellido del usuario.
	 */
	private String lastname;

	/**
	 * Nombre de usuario único.
	 */
	@Column(unique = true)
	private String username;

	/**
	 * Contraseña del usuario, no visible en las respuestas.
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	/**
	 * Documento de identidad único del usuario.
	 */
	@Column(unique = true)
	private String nationalId;

	/**
	 * Lista de roles asociados al usuario.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnoreProperties({ "users", "handler", "hibernateLazyInitializer" })
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"user_id", "role_id" }))
	@Builder.Default
	private List<Role> roles = new ArrayList<>();

	/**
	 * Cargo o posición del usuario dentro de su empresa u organización.
	 */
	@Column(name = "job_position")
	private String jobPosition;

	/**
	 * Empresa u organización del usuario.
	 */
	private String company;

	/**
	 * Ciudad de residencia.
	 */
	private String city;

	/**
	 * País de residencia.
	 */
	private String country;

	/**
	 * Dirección de correo electrónico única.
	 */
	@Column(unique = true)
	private String email;

	/**
	 * Número de teléfono de contacto.
	 */
	private String phone;

	/**
	 * Estado del usuario (habilitado o no).
	 */
	private boolean enabled;

	/**
	 * Inicializa el campo enabled como true antes de la persistencia.
	 * Inicializa la fecha de creación antes de la persistencia.
	 */
	@PrePersist
	public void prePersist() {
		enabled = true;
		createdAt = LocalDateTime.now();
	}

	/**
	 * Fecha de creación del usuario.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * Fecha de la última actualización del usuario.
	 */
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/**
	 * Fecha del último inicio de sesión del usuario.
	 */
	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	/**
	 * Indicador temporal para verificar si el usuario es administrador.
	 */
	@Transient
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean admin;

	/**
	 * Método para verificar si el usuario tiene un rol específico.
	 * 
	 * @param roleName Nombre del rol a verificar.
	 * @return true si el usuario tiene el rol especificado, de lo contrario,
	 *         false.
	 */
	public boolean hasRole(String roleName) {
		return roles != null && roles
				.stream()
				.anyMatch(role -> role.getName().name().equalsIgnoreCase(roleName));
	}

	/**
	 * Método para devolver los roles en formato JSON-friendly.
	 * 
	 * @return Una lista de objetos RoleType que representan los roles del
	 *         usuario.
	 */
	@JsonProperty("roles")
	public List<RoleType> getRoleNames() {
		return roles
				.stream()
				.map(Role::getName)
				.collect(Collectors.toList());
	}

	/**
	 * Método para asignar roles desde JSON.
	 * 
	 * @param roleNames Lista de objetos RoleType que representan los roles que se
	 *                  asignarán al usuario.
	 */
	@JsonProperty("roles")
	public void setRoleNames(List<RoleType> roleNames) {
		if (roleNames == null) {
			this.roles = new ArrayList<>();
		} else {
			this.roles = roleNames
					.stream()
					.map(Role::new)
					.collect(Collectors.toList());
		}
	}

}
