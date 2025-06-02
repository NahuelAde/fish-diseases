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
import jakarta.persistence.PrePersist;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para la creación de nuevos usuarios.
 * Incluye validaciones de formato y restricciones para cada campo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    /**
     * Nombre del usuario. Debe tener entre 2 y 50 caracteres.
     */
	@NotBlank(message = "{IsRequired.user.firstname}")
    @Size(min = 2, max = 50, message = "{Size.user.firstname}")
	private String firstname;

    /**
     * Apellido del usuario. Debe tener entre 2 y 100 caracteres.
     */
	@NotBlank(message = "{IsRequired.user.lastname}")
    @Size(min = 2, max = 100, message = "{Size.user.lastname}")
	private String lastname;

    /**
     * Nombre de usuario único. Requiere un formato alfanumérico simple.
     */
	@Column(unique = true)
	@NotBlank(message = "{IsRequired.user.username}")
	@Size(min = 3, max = 20, message = "{Size.user.username}")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "{Pattern.user.username}")
	private String username;

    /**
     * Contraseña del usuario. Solo accesible para escritura.
     * Debe cumplir requisitos de seguridad como incluir mayúsculas, minúsculas, dígitos y símbolos.
     */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "{IsRequired.user.password}")
    @Size(min = 8, max = 20, message = "{Size.user.password}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&€]+$", message = "{Pattern.user.password}")
	private String password;

    /**
     * Documento de identidad único del usuario.
     */
	@Column(unique = true)
	@NotBlank(message = "{IsRequired.user.national_id}")
	@Size(max = 20, message = "{Size.user.national_id}")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "{Pattern.user.national_id}")
	private String nationalId;

    /**
     * Lista de roles asociados al usuario.
     */
	@ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"users", "handler", "hibernateLazyInitializer"})
	@JoinTable(name = "user_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id"),
		uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role_id" }))
	private List<Role> roles;

    /**
     * Cargo o posición del usuario dentro de su empresa u organización.
     */
	@Column(name = "job_position")
    @Size(max = 50, message = "{Size.user.job_position}")
	private String jobPosition;

    /**
     * Empresa u organización del usuario.
     */
    @Size(max = 50, message = "{Size.user.company}")
	private String company;

    /**
     * Ciudad de residencia.
     */
	@NotBlank(message = "{IsRequired.user.city}")
	@Size(min = 2, max = 100, message = "{Size.user.city}")
	private String city;

    /**
     * País de residencia.
     */
	@NotBlank(message = "{IsRequired.user.country}")
	@Size(min = 2, max = 60, message = "{Size.user.country}")
	private String country;

    /**
     * Dirección de correo electrónico única.
     */
	@Column(unique = true)
	@NotBlank(message = "{IsRequired.user.email}")
    @Email(message = "{IsRequired.user.email.invalid}")
	private String email;

    /**
     * Número de teléfono de contacto.
     */
    @Size(max = 15, message = "{Size.user.phone}")
    @Pattern(regexp = "^[+]?[0-9]+$", message = "{Pattern.user.phone}")
	private String phone;

    /**
     * Estado del usuario (habilitado o no). Se establece como true por defecto.
     */
	private boolean enabled;

    /**
     * Inicializa el campo enabled como true antes de la persistencia.
     */
    @PrePersist
    public void prePersist() {
        enabled = true;
    }

}
