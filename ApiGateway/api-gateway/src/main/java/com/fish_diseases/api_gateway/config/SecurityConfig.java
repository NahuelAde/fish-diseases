package com.fish_diseases.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.fish_diseases.api_gateway.security.JwtAuthenticationWebFilter;
import com.fish_diseases.api_gateway.security.JwtUtil;

/**
 * Configuración de seguridad para la aplicación API Gateway en modo reactivo.
 * 
 * Establece la política de seguridad desactivando CSRF, definiendo sesiones sin estado
 * y configurando las reglas de acceso a los distintos endpoints. Se permite el acceso público
 * a recursos de Swagger, a los endpoints de autenticación (/auth/**) y se protege el resto.
 * Finalmente, se añade el filtro de autenticación JWT (implementado de forma reactiva)
 * para interceptar y procesar las peticiones.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Constructor de la clase {@code SecurityConfig}.
     *
     * @param jwtUtil Utilidad para gestionar la creación, validación y extracción de información de tokens JWT.
     */
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Define la cadena de filtros de seguridad para entornos reactivos.
     *
     * @param http Objeto de configuración reactiva {@code ServerHttpSecurity}.
     * @return La cadena de filtros de seguridad configurada en forma de {@code SecurityWebFilterChain}.
     */
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchange -> exchange
            		
                // Rutas de Swagger
                .pathMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                    
                // Endpoints de Users
                .pathMatchers("/auth-service/users/register", "/auth-service/users/login").permitAll()
                .pathMatchers(HttpMethod.GET, "/auth-service/users").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.GET,   "/auth-service/users/{userId}").hasAnyAuthority("ROLE_TREATMENT","ROLE_ADMIN")
                .pathMatchers(HttpMethod.POST,  "/auth-service/users/{userId}/logout").hasAnyAuthority("ROLE_TREATMENT","ROLE_ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/auth-service/users/{userId}").hasAnyAuthority("ROLE_TREATMENT","ROLE_ADMIN")
                .pathMatchers(HttpMethod.PUT, "/auth-service/users/{userId}/disable").hasAnyAuthority("ROLE_TREATMENT", "ROLE_ADMIN")
                .pathMatchers(HttpMethod.PUT, "/auth-service/users/{userId}/role-admin").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.DELETE,"/auth-service/users/{userId}").hasAuthority("ROLE_ADMIN")
                    
                // Endpoints de Parasites
                .pathMatchers(HttpMethod.GET, "/biodata-service/parasites", "/biodata-service/parasites/{parasiteId}", "/biodata-service/parasites/sn/{scientificName}").permitAll()
                .pathMatchers(HttpMethod.GET, "/biodata-service/parasites/fetch/{scientificName}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.POST, "/biodata-service/parasites").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/biodata-service/parasites/{scientificName}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/biodata-service/parasites/{scientificName}").hasAuthority("ROLE_ADMIN")
                
                // Endpoints de Fishes
                .pathMatchers(HttpMethod.GET, "/biodata-service/fishes", "/biodata-service/fishes/{fishId}", "/biodata-service/fishes/sn/{scientificName}").permitAll()
                .pathMatchers(HttpMethod.GET, "/biodata-service/fishes/fetch/{scientificName}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.POST, "/biodata-service/fishes").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/biodata-service/fishes/{scientificName}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/biodata-service/fishes/{scientificName}").hasAuthority("ROLE_ADMIN")
                
                // Endpoints de Treatments
                .pathMatchers(HttpMethod.GET, "/treatment-service/treatments", "/treatment-service/treatments/{id}", "/treatment-service/treatments/name/{treatmentName}").hasAnyAuthority("ROLE_TREATMENT", "ROLE_ADMIN")
                .pathMatchers(HttpMethod.POST, "/treatment-service/treatments").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/treatment-service/treatments/{id}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/treatment-service/treatments/{id}").hasAuthority("ROLE_ADMIN")
                
                // Endpoints de Lab Methods
                .pathMatchers(HttpMethod.GET, "/treatment-service/laboratory-methods", "/treatment-service/laboratory-methods/{id}", "/treatment-service/laboratory-methods/name/{laboratoryMethodName}").hasAnyAuthority("ROLE_TREATMENT", "ROLE_ADMIN")
                .pathMatchers(HttpMethod.POST, "/treatment-service/laboratory-methods").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/treatment-service/laboratory-methods/{id}").hasAuthority("ROLE_ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/treatment-service/laboratory-methods/{id}").hasAuthority("ROLE_ADMIN")
                
                // Cualquier otra solicitud requiere autenticación.
                .anyExchange().authenticated()
            )
            .addFilterAt(new JwtAuthenticationWebFilter(jwtUtil), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
