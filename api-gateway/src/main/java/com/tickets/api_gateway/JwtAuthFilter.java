package com.tickets.api_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final SecretKey key;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return deny(exchange, HttpStatus.UNAUTHORIZED);
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(authHeader.substring(7)).getPayload();
        } catch (Exception e) {
            return deny(exchange, HttpStatus.UNAUTHORIZED);
        }

        String role = claims.get("role", String.class);
        String userId = claims.getSubject();

        if (!authorized(path, method, role)) {
            return deny(exchange, HttpStatus.FORBIDDEN);
        }

        // stamp trusted identity; strip any spoofed client values
        ServerHttpRequest mutated = request.mutate()
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-User-Role");
                    h.add("X-User-Id", userId);
                    h.add("X-User-Role", role);
                })
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path, HttpMethod method) {
        if (path.equals("/auth/login")) return true;
        if (path.equals("/users") && method == HttpMethod.POST) return true;  // registration
        return false;
    }

    private boolean authorized(String path, HttpMethod method, String role) {
        boolean adminOp =
                (path.equals("/events") && method == HttpMethod.POST) ||      // create event
                (path.startsWith("/events/") && method == HttpMethod.PATCH);  // change qty/price
        if (adminOp) return "ADMIN".equals(role);
        return true;   // everything else: any authenticated account
    }

    private Mono<Void> deny(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;   // run before routing
    }
}