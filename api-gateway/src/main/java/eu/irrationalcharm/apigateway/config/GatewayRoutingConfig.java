package eu.irrationalcharm.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Configuration
public class GatewayRoutingConfig {

    /**
    @Bean
    public RouteLocator customRoutLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f
                                // 1. Rate Limiting (Using Redis)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(jwtUserKeyResolver))

                                // 2. Retry Logic
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.GATEWAY_TIMEOUT)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))

                                // 3. Circuit Breaker & Fallback
                                .circuitBreaker(config -> config
                                        .setName("userServiceBreaker")
                                        .setFallbackUri("forward:/fallback/user-service")))
                        .uri("http://localhost:8083"))

                .route("messaging-persistence-service", r -> r.path("/message-persistence-service/**")
                        .filters(f -> f
                                // 1. Rate Limiting (Using Redis)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(jwtUserKeyResolver))

                                // 2. Retry Logic
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.GATEWAY_TIMEOUT)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))

                                // 3. Circuit Breaker & Fallback
                                .circuitBreaker(config -> config
                                        .setName("messagingPersistenceBreaker")
                                        .setFallbackUri("forward:/fallback/message-persistence-service")))
                        .uri("http://localhost:8086"))

                .route("mobile-bff", r -> r.path("/mobile-bff/**")
                        .filters(f -> f
                                // 1. Rate Limiting (Using Redis)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(jwtUserKeyResolver))

                                // 2. Retry Logic
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.GATEWAY_TIMEOUT)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))

                                // 3. Circuit Breaker & Fallback
                                .circuitBreaker(config -> config
                                        .setName("mobileBffBreaker")
                                        .setFallbackUri("forward:/fallback/mobile-bff")))
                        .uri("http://localhost:8087"))

                .route("messaging-service-ws", r -> r.path("/ws/**")
                        .filters(f -> f
                                // 1. Rate Limiting (Using Redis)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(jwtUserKeyResolver))

                                // 2. Retry Logic
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.GATEWAY_TIMEOUT)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))

                                // 3. Circuit Breaker & Fallback
                                .circuitBreaker(config -> config
                                        .setName("messagingServiceBreaker")
                                        .setFallbackUri("forward:/fallback/mobile-bff")))
                        .uri("ws://localhost:8084"))


                .route("messaging-service-http", r -> r.path("/ws/**")
                        .filters(f -> f
                                // 1. Rate Limiting (Using Redis)
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(jwtUserKeyResolver))

                                // 2. Retry Logic
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.GATEWAY_TIMEOUT)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofSeconds(2), 2, true))

                                // 3. Circuit Breaker & Fallback
                                .circuitBreaker(config -> config
                                        .setName("mobileBffBreaker")
                                        .setFallbackUri("forward:/fallback/mobile-bff")))
                        .uri("http://localhost:8084"))

                .build();

    }**/
}
