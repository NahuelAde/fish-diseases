package com.fish_diseases.api_gateway.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * Filtro de autenticación JWT para entornos reactivos que intercepta las
 * solicitudes HTTP, extrae el token JWT del encabezado de autorización y, si es
 * válido, establece la autenticación en el contexto de seguridad reactivo
 * mediante ReactiveSecurityContextHolder.
 */
public class JwtAuthenticationWebFilter implements WebFilter {

	private final JwtUtil jwtUtil;

	/**
	 * Constructor del filtro de autenticación JWT.
	 * 
	 * @param jwtUtil Utilidad para gestionar la creación, validación y extracción
	 *                de información de los tokens JWT.
	 */
	public JwtAuthenticationWebFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	/**
	 * Filtra la solicitud HTTP de forma reactiva y autentica al usuario si se
	 * encuentra un token JWT válido.
	 * 
	 * Extrae el token del encabezado "Authorization" y, si es válido, crea una
	 * instancia de UsernamePasswordAuthenticationToken con los roles extraídos para
	 * inyectarla en el contexto de seguridad reactivo.
	 *
	 * @param exchange El objeto ServerWebExchange que encapsula la solicitud y la
	 *                 respuesta.
	 * @param chain    La cadena de filtros reactivos.
	 * @return Un Mono<Void> que completa la cadena de filtros.
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				String username = jwtUtil.extractUsername(token);
				List<String> roles = jwtUtil.extractRoles(token);
				if (username != null) {
					List<GrantedAuthority> authorities = roles
							.stream()
							.map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,
							null, authorities);

					return chain
							.filter(exchange)
							.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
				}
			} catch (Exception e) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
		}
		return chain.filter(exchange);
	}
}
