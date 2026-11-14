package br.edu.atitus.gatewayapi.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            System.err.println(">>> ERRO AO VALIDAR TOKEN: " + e.getMessage());
            return null;
        }
    }
}