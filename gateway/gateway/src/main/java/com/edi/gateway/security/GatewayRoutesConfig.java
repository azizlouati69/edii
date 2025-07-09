package com.edi.gateway.security;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    private final JwtHeaderFilter jwtHeaderFilter;

    public GatewayRoutesConfig(JwtHeaderFilter jwtHeaderFilter) {
        this.jwtHeaderFilter = jwtHeaderFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // edi-dash route with JWT filter and PreserveHostHeader filter
                .route("edi-dash-route", r -> r.path("/edi-dash/**")
                        .filters(f -> f
                                .filter(jwtHeaderFilter.apply(new JwtHeaderFilter.Config()))
                                .preserveHostHeader()
                        )
                        // Use service discovery URI (lb://) if you have discovery enabled
                        // Otherwise use direct URI (http://localhost:9999)
                        .uri("lb://edi-dash")
                )
                .route("asn-edi-route", r -> r.path("/edi-asn/**")
                        .filters(f -> f
                                .filter(jwtHeaderFilter.apply(new JwtHeaderFilter.Config()))
                                .preserveHostHeader()
                        )
                        .uri("lb://EdiASN")
                )
                // Add other routes here if needed
                .build();
    }
}
