package com.fish_diseases.api_gateway.security;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Helper de tests para generar JWTs firmados con el mismo secreto que usa el
 * auth-service. No forma parte de la app de producción.
 */
@Component
public class TestJwtHelper {

	@Value("${jwt.secret}")
	private String base64Secret;

	@Value("${jwt.expiration}")
	private long expirationMs;

	private SecretKey getKey() {
		byte[] decoded = Base64.getDecoder().decode(base64Secret);
		return Keys.hmacShaKeyFor(decoded);
	}

	/**
	 * Genera un token JWT con subject y roles.
	 * 
	 * @param username subject
	 * @param roles    lista de roles
	 */
	public String createToken(String username, List<String> roles) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMs);
		JwtBuilder builder = Jwts
				.builder()
				.subject(username)
				.claim("roles", roles)
				.issuedAt(new Date())
				.expiration(exp)
				.signWith(getKey());

		return builder.compact();
	}
}