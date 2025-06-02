package com.fish_diseases.auth_service.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fish_diseases.auth_service.entities.Role;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar los datos de un usuario existente.
 * Contiene solo los campos editables y permite cambios parciales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    /**
     * Nueva contraseña del usuario. Solo accesible para escritura.
     */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min = 8, max = 20, message = "{Size.user.password}")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "{Pattern.user.password}")
	private String password;

    /**
     * Documento de identidad actualizado.
     */
	@Column(unique = true)
	@Size(max = 20, message = "{Size.user.national_id}")
	@Pattern(regexp = "^[A-Za-z0-9]+$", message = "{Pattern.user.national_id}")
	private String nationalId;

    /**
     * Lista de roles actualizados asignados al usuario.
     */
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnoreProperties({ "users", "handler", "hibernateLazyInitializer" })
	@JoinTable(
			name = "user_roles", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "role_id"), 
			uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id" }))
	private List<Role> roles;

    /**
     * Cargo actualizado del usuario.
     */
	@Column(name = "job_position")
	@Size(max = 50, message = "{Size.user.job_position}")
	private String jobPosition;

    /**
     * Empresa actualizada del usuario.
     */
	@Size(max = 50, message = "{Size.user.company}")
	private String company;

    /**
     * Ciudad actualizada del usuario.
     */
	@Size(min = 2, max = 100, message = "{Size.user.city}")
	private String city;

    /**
     * País actualizado del usuario.
     */
	@Size(min = 2, max = 60, message = "{Size.user.country}")
	private String country;

    /**
     * Dirección de correo electrónico actualizada.
     */
	@Column(unique = true)
	@Email(message = "{IsRequired.user.email.invalid}")
	private String email;

    /**
     * Teléfono actualizado del usuario.
     */
	@Size(max = 15, message = "{Size.user.phone}")
	@Pattern(regexp = "^[+]?[0-9]+$", message = "{Pattern.user.phone}")
	private String phone;
}
