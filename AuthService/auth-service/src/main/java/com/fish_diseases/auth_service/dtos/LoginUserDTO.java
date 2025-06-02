package com.fish_diseases.auth_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para autenticar a un usuario en el sistema.
 * Contiene las credenciales necesarias para generar un token JWT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {

    /**
     * Nombre de usuario utilizado para iniciar sesión.
     */
	@NotBlank(message = "{IsRequired.user.username}")
	private String username;

    /**
     * Contraseña del usuario. Solo accesible para escritura.
     */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(message = "{IsRequired.user.password}")
	private String password;

}
