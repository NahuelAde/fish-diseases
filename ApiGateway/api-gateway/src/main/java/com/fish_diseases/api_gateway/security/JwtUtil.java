package com.fish_diseases.api_gateway.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para la gestión de JWT (JSON Web Tokens). Proporciona métodos para
 * generar, validar y extraer información de tokens JWT. Utiliza una clave
 * secreta configurada en el archivo de propiedades para la firma de los tokens.
 */
@Component
public class JwtUtil {

	/**
	 * Clave secreta utilizada para firmar los JWT. Se obtiene desde las propiedades
	 * de configuración.
	 */
    @Value("${jwt.secret}")
    private String secretKey;

	/**
	 * Obtiene la clave para la firma del JWT a partir de la clave secreta
	 * configurada.
	 * 
	 * @return Key La clave de firma para los tokens JWT.
	 */
   private Key getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

	/**
	 * Extrae las reclamaciones (claims) de un token JWT.
	 * 
	 * @param token El token JWT del cual extraer las reclamaciones.
	 * @return Claims Las reclamaciones extraídas del token.
	 */
    public Claims extractClaims(String token) {
		return Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

	/**
	 * Extrae el nombre de usuario (subject) de un token JWT.
	 * 
	 * @param token El token JWT del cual extraer el nombre de usuario.
	 * @return String El nombre de usuario extraído del token.
	 */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    
	/**
	 * Extrae los roles del usuario a partir de un token JWT.
	 * 
	 * @param token El token JWT del cual extraer los roles.
	 * @return List<String> La lista de roles extraídos del token.
	 */
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        List<?> rolesObject = claims.get("roles", List.class);
        return rolesObject.stream()
                          .map(Object::toString)
                          .collect(Collectors.toList());
    }
    
    /**
     * Valida si un token JWT es válido.
     * Un token es válido si el nombre de usuario coincide y no ha expirado.
     * 
     * @param token El token JWT a validar.
     * @param username El nombre de usuario que se debe verificar.
     * @return boolean True si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }
    
    /**
     * Verifica si un token JWT ha expirado.
     * 
     * @param token El token JWT a verificar.
     * @return boolean True si el token ha expirado, false si sigue siendo válido.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration != null && expiration.before(new Date());
    }
    
}
