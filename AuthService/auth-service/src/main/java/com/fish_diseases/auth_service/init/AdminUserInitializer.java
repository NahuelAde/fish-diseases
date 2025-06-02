package com.fish_diseases.auth_service.init;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fish_diseases.auth_service.entities.Role;
import com.fish_diseases.auth_service.entities.Role.RoleType;
import com.fish_diseases.auth_service.entities.User;
import com.fish_diseases.auth_service.repositories.RoleRepository;
import com.fish_diseases.auth_service.repositories.UserRepository;
import com.fish_diseases.auth_service.services.UserService;

/**
 * Componente encargado de inicializar un usuario administrador y un treatment
 * en la base de datos al arrancar la aplicación.
 * 
 * Esta clase implementa la interfaz CommandLineRunner, lo que permite ejecutar
 * código al inicio de la aplicación. Su principal función es asegurarse de que
 * los roles necesarios (ADMIN y TREATMENT) existen en la base de datos y crear
 * un usuario administrador con estos roles si no existe ya.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private final boolean isTraceEnabled = log.isTraceEnabled();

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	/**
	 * Constructor para la clase AdminUserInitializer.
	 * 
	 * @param userRepository  Repositorio para gestionar usuarios.
	 * @param roleRepository  Repositorio para gestionar roles.
	 * @param passwordEncoder Codificador de contraseñas para almacenar las
	 *                        contraseñas de manera segura.
	 */
	public AdminUserInitializer(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Método que se ejecuta al arrancar la aplicación.
	 * 
	 * Se encarga de verificar si existen los roles de ADMIN y TREATMENT. Si no
	 * existen, los crea en la base de datos. Luego, verifica si los usuarios
	 * 'admin' y 'treatment' existe; si no existen, los crea con los roles
	 * correspondientes y con una contraseña codificada.
	 * 
	 * @param args Argumentos de línea de comandos.
	 */
	@Override
	public void run(String... args) {

		if (isTraceEnabled) {
			log.trace("######## Start AdminUserInitializer ########");
		}

		if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
			Role adminRole = new Role();
			adminRole.setName(RoleType.ROLE_ADMIN);
			roleRepository.save(adminRole);
		}

		if (roleRepository.findByName(RoleType.ROLE_TREATMENT).isEmpty()) {
			Role treatmentRole = new Role();
			treatmentRole.setName(RoleType.ROLE_TREATMENT);
			roleRepository.save(treatmentRole);
		}

		Role adminRole = roleRepository
				.findByName(RoleType.ROLE_ADMIN)
				.orElseThrow(() -> new RuntimeException("ROLE_ADMIN no encontrado"));

		Role treatmentRole = roleRepository
				.findByName(RoleType.ROLE_TREATMENT)
				.orElseThrow(() -> new RuntimeException("ROLE_TREATMENT no encontrado"));

		if (userRepository.findByUsername("admin").isEmpty()) {

			if (isTraceEnabled) {
				log.trace("######## Start Admin User creation ########");
			}

			User adminUserInit = User
					.builder()
					.firstname("Nahuel Marcelo")
					.lastname("Ade")
					.username("admin")
					.password("Admin123*")
					.nationalId("X0859212R")
					.city("Madrid")
					.country("España")
					.email("nahuel.ade.pro@gmail.com")
					.enabled(true)
					.admin(true)
					.build();

			User admin = new User();
			admin.setUsername(adminUserInit.getUsername());
			admin.setPassword(passwordEncoder.encode(adminUserInit.getPassword()));
			admin.setFirstname(adminUserInit.getFirstname());
			admin.setLastname(adminUserInit.getLastname());
			admin.setNationalId(adminUserInit.getNationalId());
			admin.setCity(adminUserInit.getCity());
			admin.setCountry(adminUserInit.getCountry());
			admin.setEmail(adminUserInit.getEmail());
			admin.setEnabled(adminUserInit.isEnabled());

			admin.setRoles(List.of(adminRole, treatmentRole));

			userRepository.save(admin);

			if (isTraceEnabled) {
				log.trace("######## End Admin User creation ########");
			}
		}

		if (userRepository.findByUsername("treatment").isEmpty()) {

			if (isTraceEnabled) {
				log.trace("######## Start Treatment User creation ########");
			}

			User treatmentUserInit = User
					.builder()
					.firstname("Treatment")
					.lastname("User")
					.username("treatment")
					.password("Treat123*")
					.nationalId("Y1234567Z")
					.city("Barcelona")
					.country("España")
					.email("treatment@example.com")
					.enabled(true)
					.admin(false)
					.build();

			User treatment = new User();
			treatment.setUsername(treatmentUserInit.getUsername());
			treatment.setPassword(passwordEncoder.encode(treatmentUserInit.getPassword()));
			treatment.setFirstname(treatmentUserInit.getFirstname());
			treatment.setLastname(treatmentUserInit.getLastname());
			treatment.setNationalId(treatmentUserInit.getNationalId());
			treatment.setCity(treatmentUserInit.getCity());
			treatment.setCountry(treatmentUserInit.getCountry());
			treatment.setEmail(treatmentUserInit.getEmail());
			treatment.setEnabled(treatmentUserInit.isEnabled());

			treatment.setRoles(List.of(treatmentRole));

			userRepository.save(treatment);

			if (isTraceEnabled) {
				log.trace("######## End Treatment User creation ########");
			}
		}

		if (isTraceEnabled) {
			log.trace("######## End AdminUserInitializer ########");
		}
	}
}