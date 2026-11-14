package br.edu.atitus.authapi.components;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import br.edu.atitus.authapi.entities.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(String email, Long id, UserType type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("type", type.ordinal());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getJwtFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        if (jwt == null || jwt.isEmpty())
            jwt = request.getHeader("authorization");
        if (jwt != null && !jwt.isEmpty()) {
            return jwt.substring(7);
        }
        return null;
    }
}