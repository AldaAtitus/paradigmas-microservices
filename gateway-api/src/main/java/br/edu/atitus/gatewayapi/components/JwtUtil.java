package br.edu.atitus.gatewayapi.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class JwtUtil {

    private static final String SECRET_KEY = "chaveSuperSecretaParaJWTdeExemplo!@#123";

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            System.err.println(">>> ERRO AO VALIDAR TOKEN: " + e.getMessage());
            e.printStackTrace(); // Isso vai mostrar o erro exato no console
            return null;
        }
    }
}