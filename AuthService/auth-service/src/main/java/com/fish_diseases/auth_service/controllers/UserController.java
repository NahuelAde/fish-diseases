package com.fish_diseases.auth_service.controllers;

import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.BAD_REQUEST_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.BAD_REQUEST_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.CONFLICT_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.CONFLICT_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.CREATED_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.CREATED_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.FORBIDDEN_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.FORBIDDEN_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.INTERNAL_SERVER_ERROR_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.NOT_FOUND_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.NOT_FOUND_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.NO_CONTENT_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.NO_CONTENT_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.OK_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.OK_DESCRIPTION;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.UNAUTHORIZED_CODE;
import static com.fish_diseases.auth_service.featurehelper.SwaggerConstants.UNAUTHORIZED_DESCRIPTION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fish_diseases.auth_service.dtos.CreateUserDTO;
import com.fish_diseases.auth_service.dtos.LoginUserDTO;
import com.fish_diseases.auth_service.dtos.UpdateUserDTO;
import com.fish_diseases.auth_service.entities.Role;
import com.fish_diseases.auth_service.entities.User;
import com.fish_diseases.auth_service.security.JwtUtil;
import com.fish_diseases.auth_service.services.UserService;
import com.fish_diseases.auth_service.utils.ResponseUtil;
import com.fish_diseases.auth_service.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de usuarios en el sistema. Proporciona
 * endpoints para autenticación, registro, consulta, actualización, asignación
 * de roles, desactivación y eliminación de usuarios. Requiere autenticación
 * mediante JWT.
 */
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuario", description = "Operaciones relacionadas con los usuarios del sistema.")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private ResponseUtil responseUtil;

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Obtiene la lista de todos los usuarios registrados. Requiere autorización
	 * como Administrador.
	 *
	 * @return ResponseEntity con la lista de usuarios o un código de error
	 *         correspondiente.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping
	public ResponseEntity<?> list() {
		User currentUser = userService.getCurrentUser();

		if (currentUser == null || !currentUser.hasRole("ROLE_ADMIN")) {
			return responseUtil.error("error.forbiddenListUserNotAdmin", HttpStatus.FORBIDDEN);
		}

		List<User> users = userService.findAll();
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

	/**
	 * Obtiene un usuario específico por su ID. Accesible solo para el mismo usuario
	 * o un administrador.
	 *
	 * @param userId ID del usuario a buscar.
	 * @return ResponseEntity con el usuario encontrado o un error adecuado.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NO_CONTENT_CODE, description = NO_CONTENT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable Long userId) {
		User currentUser = userService.getCurrentUser();
		if (currentUser == null) {
			return responseUtil.error("error.unauthorizedAccess", HttpStatus.UNAUTHORIZED);
		}

		if (!currentUser.hasRole("ROLE_ADMIN") && !currentUser.getUserId().equals(userId)) {
			return responseUtil.error("error.forbiddenViewUser", HttpStatus.FORBIDDEN);
		}

		return userService
				.findById(userId)
				.<ResponseEntity<?>>map(ResponseEntity::ok)
				.orElseGet(() -> responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND));
	}

	//////////////////////////////////
	//// MÉTODOS DE AUTENTICACIÓN ////
	//////////////////////////////////

	/**
	 * Autentica al usuario con las credenciales proporcionadas y devuelve un token
	 * JWT.
	 *
	 * @param loginUserDTO Objeto con las credenciales de inicio de sesión.
	 * @return ResponseEntity con el token JWT y mensaje de éxito o error.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginUserDTO loginUserDTO) {
		Optional<User> authenticatedUser = userService
				.authenticateUser(loginUserDTO.getUsername(),
						loginUserDTO.getPassword());

		if (authenticatedUser.isEmpty()) {
			return responseUtil.error("error.invalidCredentials", HttpStatus.UNAUTHORIZED);
		}

		userService.updateLastLogin(authenticatedUser.get().getUserId());

		String token = jwtUtil
				.generateToken(
						authenticatedUser.get().getUsername(),
						authenticatedUser
								.get()
								.getRoleNames()
								.stream()
								.map(roleType -> roleType.name())
								.collect(Collectors.toList()));
		ResponseEntity<?> baseResponse = responseUtil.success("message.loginSuccessful", HttpStatus.OK);

		Map<String, Object> response = new HashMap<>();
		response.put("message", ((Map<?, ?>) baseResponse.getBody()).get("message"));
		response.put("token", token);

		return ResponseEntity.ok(response);
	}

	/**
	 * Logout de usuario (elimina el token del cliente)
	 * 
	 * @return ResponseEntity
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		User currentUser = userService.getCurrentUser();

		if (currentUser == null) {
			return responseUtil.error("error.unauthorizedAccess", HttpStatus.UNAUTHORIZED);
		}

		return responseUtil.success("message.logoutSuccessful", HttpStatus.OK);
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Crea un nuevo usuario con los datos proporcionados.
	 *
	 * @param createUserDTO Datos del nuevo usuario.
	 * @param result        Resultado de la validación.
	 * @return ResponseEntity con el usuario creado o error de validación/conflicto.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = CREATED_CODE, description = CREATED_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = BAD_REQUEST_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PostMapping("/register")
	public ResponseEntity<?> create(@Valid @RequestBody CreateUserDTO createUserDTO, BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		if (userService.existsByUsername(createUserDTO.getUsername())) {
			return responseUtil.error("error.username.exists", HttpStatus.CONFLICT);
		}

		if (userService.existsByNationalId(createUserDTO.getNationalId())) {
			return responseUtil.error("error.nationalId.exists", HttpStatus.CONFLICT);
		}

		if (userService.existsByEmail(createUserDTO.getEmail())) {
			return responseUtil.error("error.email.exists", HttpStatus.CONFLICT);
		}

		createUserDTO.setRoles(List.of(new Role("ROLE_TREATMENT")));

		User createdUser = userService.save(createUserDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Asigna o revoca el rol de administrador a un usuario según su estado actual.
	 * Solo permitido para administradores y no sobre sí mismos.
	 *
	 * @param userId ID del usuario a modificar.
	 * @return ResponseEntity con mensaje de éxito o error correspondiente.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PutMapping("/{userId}/role-admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> toggleAdminRole(@PathVariable Long userId) {
		User currentUser = userService.getCurrentUser();

		if (currentUser.getUserId().equals(userId)) {
			return responseUtil.error("error.cannotRemoveOwnAdmin", HttpStatus.FORBIDDEN);
		}

		return userService.findById(userId).map(user -> {
			boolean isAdmin = user.hasRole("ROLE_ADMIN");

			if (isAdmin) {
				userService.removeRole(userId, "ROLE_ADMIN");
			} else {
				userService.updateRole(userId, "ROLE_ADMIN");
			}

			return responseUtil.success(isAdmin ? "message.roleRevoked" : "message.roleAssigned", HttpStatus.OK);
		}).orElseGet(() -> responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND));
	}

	/**
	 * Actualiza los datos de un usuario existente.
	 *
	 * @param userId        ID del usuario a actualizar.
	 * @param updateUserDTO Nuevos datos del usuario.
	 * @param result        Resultado de la validación.
	 * @return ResponseEntity con mensaje de éxito o error correspondiente.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = CONFLICT_CODE, description = CONFLICT_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PatchMapping("/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserDTO updateUserDTO,
			BindingResult result) {
		if (result.hasFieldErrors()) {
			return ValidationUtils.buildValidationError(result, responseUtil);
		}

		return userService
				.update(userId, updateUserDTO)
				.map(currentUser -> responseUtil.success("message.user.updated", HttpStatus.OK))
				.orElseGet(() -> responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND));
	}

	//////////////////////////////////////////////////
	//// MÉTODOS DE DESHABILITACIÓN Y ELIMINACIÓN ////
	//////////////////////////////////////////////////

	/**
	 * Alterna el estado de habilitación de un usuario (habilitado ↔ deshabilitado).
	 * El propio usuario puede deshabilitarse; un administrador puede des/habilitar
	 * a cualquier usuario.
	 *
	 * @param userId ID del usuario a modificar.
	 * @return ResponseEntity con mensaje de éxito o error correspondiente.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = UNAUTHORIZED_CODE, description = UNAUTHORIZED_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@PutMapping("/{userId}/disable")
	public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
		
	    Optional<User> userOptional = userService.findById(userId);

	    if (userOptional.isEmpty()) {
	        return responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND);
	    }

	    User user = userOptional.get();
	    boolean wasEnabled = user.isEnabled();

	    Optional<User> updatedUser = userService.toggleUserStatus(userId);

	    if (updatedUser.isEmpty()) {
	        String errorKey = wasEnabled ? "error.forbiddenDisableUser" : "error.forbiddenEnableUser";
	        return responseUtil.error(errorKey, HttpStatus.FORBIDDEN);
	    }

	    boolean isNowEnabled = updatedUser.get().isEnabled();
	    String message = isNowEnabled ? "message.user.reactivated" : "message.user.disabled";

	    return responseUtil.success(message, HttpStatus.OK);
	}

	/**
	 * Elimina un usuario si está deshabilitado. Solo accesible por un
	 * administrador.
	 *
	 * @param userId ID del usuario a eliminar.
	 * @return ResponseEntity con mensaje de éxito o error correspondiente.
	 */
	@ApiResponses({
			@ApiResponse(responseCode = OK_CODE, description = OK_DESCRIPTION, content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)), mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)),
			@ApiResponse(responseCode = NOT_FOUND_CODE, description = NOT_FOUND_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = FORBIDDEN_CODE, description = FORBIDDEN_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = BAD_REQUEST_CODE, description = "El usuario debe estar deshabilitado antes de ser eliminado", content = @Content(schema = @Schema(hidden = true))),
			@ApiResponse(responseCode = INTERNAL_SERVER_ERROR_CODE, description = INTERNAL_SERVER_ERROR_DESCRIPTION, content = @Content(schema = @Schema(hidden = true))) })
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> delete(@PathVariable Long userId) {
		User currentUser = userService.getCurrentUser();

		if (currentUser == null || !currentUser.hasRole("ROLE_ADMIN")) {
			return responseUtil.error("error.forbiddenDelete", HttpStatus.FORBIDDEN);
		}

		Optional<User> optionalUser = userService.findById(userId);

		if (optionalUser.isEmpty()) {
			return responseUtil.error("error.userNotFound", HttpStatus.NOT_FOUND);
		}

		User userToDelete = optionalUser.get();

		if (userToDelete.isEnabled()) {
			return responseUtil.error("user.mustBeDisabledToDelete", HttpStatus.BAD_REQUEST);
		}

		userService.deleteUser(userId);
		return responseUtil.success("message.user.deleted", HttpStatus.OK);
	}
}
