package com.fish_diseases.auth_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
* Configuración de seguridad de Spring para la aplicación.
* Configura la autenticación, autorización y la protección de rutas HTTP utilizando JWT.
* Además, define los detalles de la gestión de usuarios y las reglas de autorización
* para diferentes roles de usuario.
* 
* Configura las políticas de seguridad para acceder a los recursos de la API, incluyendo
* la autenticación basada en JWT y la encriptación de contraseñas.
*/
@Configuration
public class SpringSecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsService userDetailsService;

    /**
     * Constructor de la clase {@link SpringSecurityConfig}.
     * 
     * @param jwtAuthenticationFilter Filtro personalizado para autenticar solicitudes con JWT.
     * @param userDetailsService Servicio para cargar los detalles de los usuarios.
     */
	public SpringSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
			UserDetailsService userDetailsService) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsService = userDetailsService;
	}

    /**
     * Bean para la codificación de contraseñas utilizando {@link BCryptPasswordEncoder}.
     * 
     * @return Instancia de {@link PasswordEncoder} para la encriptación de contraseñas.
     */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    /**
     * Bean para el {@link AuthenticationManager} que gestiona la autenticación de los usuarios.
     * 
     * @param authenticationConfiguration Configuración de autenticación de Spring.
     * @return Instancia de {@link AuthenticationManager}.
     * @throws Exception En caso de error en la configuración.
     */
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

    /**
     * Configura las reglas de seguridad HTTP, incluyendo la desactivación de CSRF, 
     * la política de sesiones y las reglas de autorización para los endpoints de la API.
     * 
     * @param http Configuración de seguridad HTTP.
     * @param authenticationEntryPoint Punto de entrada para la gestión de excepciones de autenticación.
     * @return Configuración de seguridad HTTP final.
     * @throws Exception En caso de error en la configuración de seguridad.
     */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint authenticationEntryPoint)
			throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
					    .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/health").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/register", "/users/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/logout").authenticated()
						.requestMatchers(HttpMethod.GET, "/users/{userId}").hasAnyRole("TREATMENT", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/users/{userId}/role-admin").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/users/{userId}").hasAnyRole("TREATMENT", "ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/users/{userId}").hasAnyRole("TREATMENT", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN").anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

    /**
     * Bean para configurar el {@link DaoAuthenticationProvider}, que utiliza
     * el servicio {@link UserDetailsService} para la autenticación basada en usuario.
     * 
     * @return Instancia de {@link DaoAuthenticationProvider} configurada.
     */
 	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
}
