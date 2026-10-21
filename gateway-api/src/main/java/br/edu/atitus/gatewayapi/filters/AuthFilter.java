package br.edu.atitus.gatewayapi.filters;

import br.edu.atitus.gatewayapi.components.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PROTECTED_ROUTES = List.of("/ws/");
    private static final List<String> USER_HEADERS = List.of("X-User-Id", "X-User-Type", "X-User-Email");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 1. Remove cabeçalhos de identidade enviados pelo cliente (evita falsificação)
        ServerHttpRequest sanitizedRequest = request.mutate()
                .headers(headers -> USER_HEADERS.forEach(headers::remove))
                .build();

        // 2. Verifica se a rota é protegida
        String path = request.getURI().getPath();
        boolean isProtected = PROTECTED_ROUTES.stream().anyMatch(path::startsWith);

        // 3. Rotas públicas: passa sem identidade
        if (!isProtected) {
            return chain.filter(exchange.mutate().request(sanitizedRequest).build());
        }

        // 4. Rotas protegidas: valida o JWT
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        String jwt = authHeader.substring(7);
        Claims claims = JwtUtil.validateToken(jwt);
        if (claims == null) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        // 5. Extrai dados do usuário e propaga via headers
        Long userId = claims.get("id", Long.class);
        Integer userType = claims.get("type", Integer.class);
        String email = claims.get("email", String.class);

        if (userId == null || userType == null || email == null) {
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return exchange.getResponse().setComplete();
        }

        ServerHttpRequest authenticatedRequest = sanitizedRequest.mutate()
                .headers(headers -> {
                    headers.set("X-User-Id", userId.toString());
                    headers.set("X-User-Type", userType.toString());
                    headers.set("X-User-Email", email);
                })
                .build();

        return chain.filter(exchange.mutate().request(authenticatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}