package eu.irrationalcharm.apigateway.dto;

import java.time.Instant;


public record ErrorResponseDto<T>(
        String type,
        String code,
        int status,
        String detail,
        T error,
        String instance,
        Instant timestamp) {

}
