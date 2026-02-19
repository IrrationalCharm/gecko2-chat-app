package eu.irrationalcharm.apigateway.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public class ApiResponse{
    public static <T> ResponseEntity<ErrorResponseDto<T>> error(HttpStatus httpStatus, String code, String detail, T payload) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponseDto<>(
                        "about:blank",
                            code,
                            httpStatus.value(),
                            detail,
                            payload,
                            null,
                            Instant.now()
        ));
    }
}
