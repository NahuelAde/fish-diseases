package com.fish_diseases.auth_service.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fish_diseases.auth_service.dtos.CreateUserDTO;
import com.fish_diseases.auth_service.dtos.UpdateUserDTO;
import com.fish_diseases.auth_service.entities.Role;
import com.fish_diseases.auth_service.entities.Role.RoleType;
import com.fish_diseases.auth_service.entities.User;
import com.fish_diseases.auth_service.exceptions.UserNotFoundException;
import com.fish_diseases.auth_service.mapper.UserMapper;
import com.fish_diseases.auth_service.repositories.RoleRepository;
import com.fish_diseases.auth_service.repositories.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Servicio para la gestión de usuarios. Incluye operaciones de creación,
 * actualización, asignación de roles, deshabilitación y eliminación de
 * usuarios, con verificaciones de permisos.
 */
@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private UserMapper userMapper;
	private PasswordEncoder passwordEncoder;
	private MessageSource messageSource;
	private Validator validator;

	/**
	 * Constructor de la clase UserService.
	 * 
	 * @param userRepository  Repositorio para la gestión de usuarios.
	 * @param roleRepository  Repositorio para la gestión de roles.
	 * @param userMapper      Mapeador para la conversión entre objetos DTO y
	 *                        entidades.
	 * @param passwordEncoder Codificador de contraseñas.
	 * @param messageSource   Fuente de mensajes para la internacionalización.
	 * @param validator       Validador para las entidades.
	 */
	public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper,
			PasswordEncoder passwordEncoder, MessageSource messageSource, Validator validator) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.messageSource = messageSource;
		this.validator = validator;
	}

	/////////////////////////////
	//// MÉTODOS DE CONSULTA ////
	/////////////////////////////

	/**
	 * Recupera todos los usuarios del sistema.
	 * 
	 * @return Lista de todos los usuarios registrados en el sistema.
	 */
	@Transactional(readOnly = true)
	public List<User> findAll() {

		if (isTraceEnabled) {
			log.trace("######## Start findAll User ########");
		}

		List<User> allUsers = userRepository.findAll();

		if (isTraceEnabled) {
			log.trace("######## End findAll User ########");
		}

		return allUsers;
	}

	/**
	 * Recupera un usuario por su ID.
	 * 
	 * @param userId ID del usuario a buscar.
	 * @return Un Optional que contiene al usuario si existe.
	 */
	@Transactional(readOnly = true)
	public Optional<User> findById(Long userId) {

		if (isTraceEnabled) {
			log.trace("######## Start findById User ########");
		}

		Optional<User> optUser = userRepository
				.findById(userId)
				.or(() -> {
					throw new UserNotFoundException();
				});

		if (isTraceEnabled) {
			log.trace("######## End findById User ########");
		}

		return optUser;
	}

	/**
	 * Recupera un usuario por su nombre de usuario.
	 * 
	 * @param username Nombre de usuario del usuario a buscar.
	 * @return Un Optional que contiene al usuario si existe.
	 */
	@Transactional(readOnly = true)
	public Optional<User> findByUsername(String username) {

		if (isTraceEnabled) {
			log.trace("######## Start findByUsername ########");
		}

		Optional<User> optUser = userRepository
				.findByUsername(username)
				.or(() -> {
					throw new NoSuchElementException();
				});

		if (isTraceEnabled) {
			log.trace("######## End findByUsername ########");
		}

		return optUser;
	}

	/**
	 * Recupera todos los usuarios con un rol específico.
	 * 
	 * @param roleType Tipo de rol por el cual se filtra a los usuarios.
	 * @return Lista de usuarios que tienen el rol especificado.
	 */
	@Transactional(readOnly = true)
	public List<User> findByRole(Role roleType) {

		if (isTraceEnabled) {
			log.trace("######## Start findByRole User ########");
		}

		List<User> usersByRole = userRepository.findByRoles_Name(roleType);

		if (isTraceEnabled) {
			log.trace("######## End findByRole User ########");
		}

		return usersByRole;
	}

	/**
	 * Verifica si un nombre de usuario ya existe en la base de datos.
	 * 
	 * @param username Nombre de usuario a verificar.
	 * @return `true` si el nombre de usuario ya existe, `false` en caso contrario.
	 */
	public boolean existsByUsername(String username) {

		if (isTraceEnabled) {
			log.trace("######## Start existsByUsername User ########");
		}

		boolean userByUsername = userRepository.existsByUsername(username);

		if (isTraceEnabled) {
			log.trace("######## End existsByUsername User ########");
		}

		return userByUsername;
	}

	/**
	 * Verifica si un correo electrónico ya está registrado en la base de datos.
	 * 
	 * @param email Correo electrónico a verificar.
	 * @return `true` si el correo electrónico ya existe, `false` en caso contrario.
	 */
	public boolean existsByEmail(String email) {

		if (isTraceEnabled) {
			log.trace("######## Start existsByEmail User ########");
		}

		boolean userByEmail = userRepository.existsByEmail(email);

		if (isTraceEnabled) {
			log.trace("######## End existsByEmail User ########");
		}

		return userByEmail;
	}

	/**
	 * Verifica si un número de identificación nacional ya está registrado en la
	 * base de datos.
	 * 
	 * @param nationalId Número de identificación nacional a verificar.
	 * @return `true` si el número de identificación ya existe, `false` en caso
	 *         contrario.
	 */
	public boolean existsByNationalId(String nationalId) {

		if (isTraceEnabled) {
			log.trace("######## Start existsByNationalId User ########");
		}

		boolean userByNationalId = userRepository.existsByNationalId(nationalId);

		if (isTraceEnabled) {
			log.trace("######## End existsByNationalId User ########");
		}

		return userByNationalId;
	}

	//////////////////////////////////
	//// MÉTODOS DE AUTENTICACIÓN ////
	//////////////////////////////////

	/**
	 * Verifica si las credenciales de un usuario son correctas para iniciar sesión.
	 * 
	 * @param username Nombre de usuario.
	 * @param password Contraseña proporcionada por el usuario.
	 * @return Un `Optional` que contiene al usuario si las credenciales son
	 *         correctas, de lo contrario, un Optional vacío.
	 */
	@Transactional(readOnly = true)
	public Optional<User> authenticateUser(String username, String password) {

		if (isTraceEnabled) {
			log.trace("######## Start authenticateUser ########");
		}

		Optional<User> userOptional = userRepository.findByUsername(username);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (passwordEncoder.matches(password, user.getPassword())) {
				return Optional.of(user);
			}
		}

		if (isTraceEnabled) {
			log.trace("######## End authenticateUser ########");
		}

		return Optional.empty();
	}

	/**
	 * Actualiza la fecha del último inicio de sesión de un usuario.
	 * 
	 * @param userId ID del usuario cuya fecha de último inicio de sesión se
	 *               actualizará.
	 */
	@Transactional
	public void updateLastLogin(Long userId) {

		if (isTraceEnabled) {
			log.trace("######## Start updateLastLogin ########");
		}

		userRepository.updateLastLogin(userId, LocalDateTime.now());

		if (isTraceEnabled) {
			log.trace("######## End updateLastLogin ########");
		}

	}

	/**
	 * Obtiene el usuario actualmente autenticado en el sistema.
	 * 
	 * @return El usuario autenticado.
	 * @throws BadCredentialsException Si el usuario no está autenticado.
	 */
	@Transactional(readOnly = true)
	public User getCurrentUser() {

		if (isTraceEnabled) {
			log.trace("######## Start getCurrentUser ########");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User authUser = Optional
				.ofNullable(authentication)
				.filter(auth -> auth.getPrincipal() instanceof UserDetails)
				.map(auth -> (UserDetails) auth.getPrincipal())
				.map(UserDetails::getUsername)
				.flatMap(this::findByUsername)
				.orElseThrow(() -> new BadCredentialsException(
						messageSource.getMessage("error.userNotAuthenticated", null, Locale.getDefault())));

		if (isTraceEnabled) {
			log.trace("######## End getCurrentUser ########");
		}

		return authUser;
	}

	/////////////////////////////
	//// MÉTODOS DE CREACIÓN ////
	/////////////////////////////

	/**
	 * Crea un nuevo usuario en el sistema.
	 * 
	 * @param createUserDTO Objeto DTO que contiene los datos para crear un nuevo
	 *                      usuario.
	 * @return El usuario recién creado.
	 */
	@Transactional
	public User save(CreateUserDTO createUserDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start save User ########");
		}

		Role treatmentRole = roleRepository
				.findByName(RoleType.ROLE_TREATMENT)
				.orElseThrow(IllegalArgumentException::new);

		createUserDTO.setRoles(List.of(treatmentRole));

		createUserDTO.setEnabled(true);

		User user = userMapper.convertCreateUserDtoToUserEntity(createUserDTO);

		user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

		if (isTraceEnabled) {
			log.trace("######## End save User ########");
		}

		return userRepository.save(user);
	}

	//////////////////////////////////
	//// MÉTODOS DE ACTUALIZACIÓN ////
	//////////////////////////////////

	/**
	 * Actualiza la información de un usuario.
	 * 
	 * @param userId        ID del usuario a actualizar.
	 * @param updateUserDTO DTO con los nuevos datos del usuario.
	 * @return El usuario actualizado.
	 * @throws AccessDeniedException Si el usuario no tiene permisos para realizar
	 *                               la actualización.
	 */
	@Transactional
	public Optional<User> update(Long userId, UpdateUserDTO updateUserDTO) {

		if (isTraceEnabled) {
			log.trace("######## Start update User ########");
		}

		User userDb = userRepository
				.findById(userId)
				.orElseThrow(UserNotFoundException::new);

		User currentUser = getCurrentUser();

		if (currentUser == null) {
			throw new AccessDeniedException(
					messageSource.getMessage("error.userNotAuthenticated", null, Locale.getDefault()));
		}

		if (!currentUser.getUserId().equals(userId) && !currentUser.hasRole("ROLE_ADMIN")) {
			throw new AccessDeniedException(
					messageSource.getMessage("error.unauthorizedAccess", null, Locale.getDefault()));
		}

		AtomicBoolean isUpdated = new AtomicBoolean(false);

		updateIfChangedAndValidate(updateUserDTO.getEmail(), userDb::getEmail, userDb::setEmail, "email", userDb,
				isUpdated);
		updateIfChangedAndValidate(updateUserDTO.getPhone(), userDb::getPhone, userDb::setPhone, "phone", userDb,
				isUpdated);
		updateIfChangedAndValidate(updateUserDTO.getJobPosition(), userDb::getJobPosition, userDb::setJobPosition,
				"jobPosition", userDb, isUpdated);
		updateIfChangedAndValidate(updateUserDTO.getCompany(), userDb::getCompany, userDb::setCompany, "company",
				userDb, isUpdated);
		updateIfChangedAndValidate(updateUserDTO.getCity(), userDb::getCity, userDb::setCity, "city", userDb,
				isUpdated);
		updateIfChangedAndValidate(updateUserDTO.getCountry(), userDb::getCountry, userDb::setCountry, "country",
				userDb, isUpdated);

		if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(updateUserDTO.getPassword());
			updateIfChangedAndValidate(encodedPassword, userDb::getPassword, userDb::setPassword, "password", userDb,
					isUpdated);
		}

//		if (updateUserDTO.isEnabled() != userDb.isEnabled()) {
//			if (currentUser.hasRole("ROLE_ADMIN")) {
//				userDb.setEnabled(updateUserDTO.isEnabled());
//				isUpdated.set(true);
//			} else {
//				throw new AccessDeniedException(
//						messageSource.getMessage("error.unauthorizedAccess", null, Locale.getDefault()));
//			}
//		}

		userDb.setUpdatedAt(LocalDateTime.now());
		User savedUser = userRepository.save(userDb);

		if (isTraceEnabled) {
			log.trace("######## End update User ########");
		}

		return Optional.of(savedUser);
	}

	/**
	 * Actualiza el rol de un usuario, solo si el rol que se intenta asignar es
	 * ROLE_ADMIN. Solo un usuario con el rol ROLE_ADMIN puede asignar este rol a
	 * otros usuarios.
	 * 
	 * @param userId  ID del usuario al que se le actualizará el rol.
	 * @param newRole Nuevo rol que se quiere asignar al usuario.
	 * @return Un Optional que contiene el usuario actualizado, o vacío si no se
	 *         pudo realizar la actualización.
	 */
	public Optional<User> updateRole(Long userId, String newRole) {

		if (isTraceEnabled) {
			log.trace("######## Start updateRole User ########");
		}

		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isEmpty()) {
			return Optional.empty();
		}

		User user = userOptional.get();

		User currentUser = getCurrentUser();
		if (currentUser == null || !currentUser.hasRole("ROLE_ADMIN")) {
			return Optional.empty();
		}

		if (!newRole.equals("ROLE_ADMIN")) {
			return Optional.empty();
		}

		Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_ADMIN);
		if (optionalRole.isEmpty()) {
			return Optional.empty();
		}

		Role role = optionalRole.get();

		if (user.getRoles().contains(role)) {
			return Optional.of(user);
		}

		user.getRoles().add(role);
		userRepository.save(user);

		if (isTraceEnabled) {
			log.trace("######## End updateRole User ########");
		}

		return Optional.of(user);
	}

	//////////////////////////////////////////////////
	//// MÉTODOS DE DESHABILITACIÓN Y ELIMINACIÓN ////
	//////////////////////////////////////////////////

	/**
	 * Elimina el rol "ROLE_ADMIN" de un usuario, siempre y cuando el usuario actual
	 * tenga permisos de ADMIN.
	 * 
	 * @param userId       ID del usuario al que se le eliminará el rol.
	 * @param roleToRemove Rol que se desea eliminar del usuario.
	 * @return Un Optional que contiene el usuario actualizado, o vacío si no se
	 *         pudo realizar la eliminación.
	 */
	public Optional<User> removeRole(Long userId, String roleToRemove) {

		if (isTraceEnabled) {
			log.trace("######## Start removeRole User ########");
		}

		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isEmpty()) {
			return Optional.empty();
		}

		User user = userOptional.get();

		User currentUser = getCurrentUser();
		if (currentUser == null || !currentUser.hasRole("ROLE_ADMIN")) {
			return Optional.empty();
		}

		if (!roleToRemove.equals("ROLE_ADMIN")) {
			return Optional.empty();
		}

		Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_ADMIN);
		if (optionalRole.isEmpty()) {
			return Optional.empty();
		}

		Role role = optionalRole.get();

		if (!user.getRoles().contains(role)) {
			return Optional.of(user);
		}

		user.getRoles().remove(role);
		userRepository.save(user);

		if (isTraceEnabled) {
			log.trace("######## End removeRole User ########");
		}

		return Optional.of(user);
	}

	/**
	 * Deshabilita o habilita un usuario en función de su estado actual.
	 * 
	 * @param userId ID del usuario a deshabilitar o habilitar.
	 * @return El usuario con su estado actualizado.
	 */
	@Transactional
	public Optional<User> toggleUserStatus(Long targetUserId) {

		User currentUser = getCurrentUser();
	    if (currentUser == null) {
	        return Optional.empty();
	    }

	    Optional<User> userOptional = userRepository.findById(targetUserId);
	    if (userOptional.isEmpty()) {
	        return Optional.empty();
	    }
	    User targetUser = userOptional.get();

	    boolean isAdmin    = currentUser.hasRole("ROLE_ADMIN");
	    boolean isSelf     = currentUser.getUserId().equals(targetUserId);
	    boolean isEnabled  = targetUser.isEnabled();

	    Boolean newStatus = null;

	    if (isAdmin) {
	        newStatus = !isEnabled;
	        
	    } else if (isSelf && isEnabled) {
	        newStatus = false;
	        
	    } else {
	        return Optional.empty();
	    }

	    targetUser.setEnabled(newStatus);
	    targetUser.setUpdatedAt(LocalDateTime.now());
	    userRepository.save(targetUser);

	    return Optional.of(targetUser);
	}

	
	/**
	 * Elimina un usuario del sistema solo si está deshabilitado.
	 * 
	 * @param userId ID del usuario a eliminar.
	 * @return El usuario eliminado.
	 * @throws IllegalStateException Si el usuario está habilitado y no puede ser
	 *                               eliminado.
	 */
	@Transactional
	public Optional<User> deleteUser(Long userId) {

		if (isTraceEnabled) {
			log.trace("######## Start deleteUser ########");
		}

		User userDb = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		if (userDb.isEnabled()) {
			throw new IllegalStateException(messageSource
					.getMessage("user.mustBeDisabledToDelete",
							null, Locale.getDefault()));
		}

		userRepository.deleteById(userDb.getUserId());

		if (isTraceEnabled) {
			log.trace("######## End deleteUser ########");
		}

		return Optional.of(userDb);
	}

	////////////////////////////
	//// MÉTODOS AUXILIARES ////
	////////////////////////////

	/**
	 * Método auxiliar para actualizar un campo solo si ha cambiado y validarlo.
	 * 
	 * @param newValue  Nuevo valor para el campo.
	 * @param getter    Función para obtener el valor actual del campo.
	 * @param setter    Función para establecer el nuevo valor del campo.
	 * @param fieldName Nombre del campo a validar.
	 * @param userDb    Usuario que se está actualizando.
	 * @param isUpdated Indica si el campo ha sido actualizado.
	 */
	private void updateIfChangedAndValidate(String newValue, Supplier<String> getter, Consumer<String> setter,
			String fieldName, User userDb, AtomicBoolean isUpdated) {
		if (newValue != null && !Objects.equals(newValue, getter.get())) {
			Set<ConstraintViolation<User>> violations = validator.validateValue(User.class, fieldName, newValue);
			if (!violations.isEmpty()) {
				throw new IllegalArgumentException(violations.iterator().next().getMessage());
			}
			setter.accept(newValue);
			isUpdated.set(true);
		}
	}

}
