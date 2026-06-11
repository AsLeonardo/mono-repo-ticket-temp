package com.tickets.api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order-service", r -> r.path("/orders/**", "/users/**", "/auth/**")
                .uri("http://order-service:8081"))
            .route("event-service", r -> r.path("/events/**", "/whoami")
                .uri("http://nginx-lb:8080"))
            .route("notification-service", r -> r.path("/notifications/**")
                .uri("http://notification-service:8082"))
            .build();
    }
}