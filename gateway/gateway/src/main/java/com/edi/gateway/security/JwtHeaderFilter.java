package com.edi.gateway.security;

import com.edi.gateway.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtHeaderFilter extends AbstractGatewayFilterFactory<JwtHeaderFilter.Config> {

    private final JwtService jwtService;

    public JwtHeaderFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    public static class Config {
        // No config properties needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return setUnauthorizedResponse(exchange, "Missing or invalid Authorization header");
            }

            String jwt = authHeader.substring(7);

            try {
                if (!jwtService.validateToken(jwt)) {
                    if (jwtService.isTokenExpired(jwt)) {
                        return setUnauthorizedResponse(exchange, "Token has expired");
                    }
                    return setUnauthorizedResponse(exchange, "Invalid JWT token");
                }

                Claims claims = jwtService.extractAllClaims(jwt);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.headers(h -> {
                            h.add("X-User-Name", username);
                            h.add("X-User-Id", userId.toString());
                        }))
                        .build();

                return chain.filter(mutatedExchange);
            } catch (Exception e) {
                return setUnauthorizedResponse(exchange, "JWT processing failed: " + e.getMessage());
            }
        };
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = "{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}";
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes())));
    }
}