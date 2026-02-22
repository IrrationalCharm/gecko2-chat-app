package eu.irrationalcharm.apigateway.controller;

import eu.irrationalcharm.apigateway.dto.ApiResponse;
import eu.irrationalcharm.apigateway.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    private static final Logger log = LoggerFactory.getLogger(GatewayFallbackController.class);

    @RequestMapping("/user-service") //RequestMapping allows us to cover all HTTP request types
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> userServiceFallback(ServerWebExchange exchange) {
        return handleFallback(exchange, "User Service");
    }

    @RequestMapping("/persistence")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> persistenceFallback(ServerWebExchange exchange) {
        return handleFallback(exchange, "Message Persistence Service");
    }

    @RequestMapping("/mobile-bff")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> bffFallback(ServerWebExchange exchange) {
        return handleFallback(exchange, "Mobile BFF");
    }

    @RequestMapping("/media")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> mediaFallback(ServerWebExchange exchange) {
        return handleFallback(exchange, "Media Service");
    }

    @RequestMapping("/messaging")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> messagingFallback(ServerWebExchange exchange) {
        return handleFallback(exchange, "Messaging Service");
    }


    /**
     * Unified reactive fallback handler that extracts the User ID
     */
    private Mono<ResponseEntity<ErrorResponseDto<Void>>> handleFallback(ServerWebExchange exchange, String serviceName) {
        Throwable exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(jwt -> jwt.getClaimAsString("internal_id")) // Get the user's internal ID
                .defaultIfEmpty("Anonymous/Unauthenticated")
                .doOnNext(userId -> {
                    if (exception != null) {
                        log.error("Circuit breaker fallback triggered for {} due to: {}. Affected User: {}",
                                serviceName, exception.getMessage(), userId);
                    } else {
                        log.warn("Circuit breaker fallback triggered for {}. Affected User: {}",
                                serviceName, userId);
                    }
                })
                .flatMap(_ -> buildResponse(serviceName));
    }

    // Helper method to build the 503 response
    private Mono<ResponseEntity<ErrorResponseDto<Void>>> buildResponse(String serviceName) {
        return Mono.just(
                ApiResponse.error(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                HttpStatus.SERVICE_UNAVAILABLE.toString(),
                                String.format("Service %s is not responding", serviceName),
                                null)
        );
    }

}
