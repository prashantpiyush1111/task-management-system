package com.TaskManagement.Security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import com.TaskManagement.Enum.Permission;
import com.TaskManagement.Entity.UserAuth;

@Component
public class JWTUtil {

	private final Key key;
	private final long validityTime = 12 * 60 * 60 * 1000L; // 12 hours

	public JWTUtil() {

		String secret = System.getenv("JWT_SECRET");

		if (secret == null || secret.isBlank()) {
			 throw new RuntimeException("JWT_SECRET environment variable not set");
		}

		key = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(UserAuth user) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole().name());

		Set<Permission> permissions = RolebasedPermissionConfig.getrole_permission().get(user.getRole());

		if (permissions != null) {
			List<String> permNames = permissions.stream().map(Enum::name).collect(Collectors.toList());

			claims.put("permissions", permNames);
		}

		Date now = new Date();
		Date expiry = new Date(now.getTime() + validityTime);

		return Jwts.builder().setSubject(user.getUserOfficialEmail()).setIssuedAt(now).setExpiration(expiry)
				.addClaims(claims).signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public boolean validateToken(String token) {

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public String getUserEmail(String token) {
		return getClaims(token).getSubject();
	}

	public String extractToken(String header) {
		if (header != null && header.startsWith("Bearer ")) {
			return header.substring(7);
		}
		return null;
	}
}