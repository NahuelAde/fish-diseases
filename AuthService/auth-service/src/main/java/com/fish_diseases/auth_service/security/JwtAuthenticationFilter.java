package com.fish_diseases.auth_service.security;

import java.io.IOException;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT para validar los tokens de autenticación
 * en las solicitudes HTTP. Este filtro intercepta las solicitudes para
 * verificar la validez del token JWT y establece la autenticación en
 * el contexto de seguridad si el token es válido.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;


    /**
     * Constructor del filtro de autenticación JWT.
     * 
     * @param jwtUtil Utilidad para la gestión de JWT.
     * @param userDetailsService Servicio para cargar los detalles del usuario.
     * @param messageSource Fuente de mensajes para la internacionalización.
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, MessageSource messageSource) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
		this.messageSource = messageSource;
    }

    /**
     * Método principal del filtro que intercepta la solicitud HTTP y valida
     * el token JWT. Si el token es válido, se establece la autenticación en
     * el contexto de seguridad.
     * 
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param chain La cadena de filtros que se ejecutarán después de este filtro.
     * @throws ServletException Si ocurre un error al procesar la solicitud.
     * @throws IOException Si ocurre un error al procesar la entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/users/register") || path.equals("/users/login")) {
            chain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            String errorMessage = messageSource.getMessage("error.invalidToken", null, Locale.getDefault());
            throw new IllegalArgumentException(errorMessage, e);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }
}
