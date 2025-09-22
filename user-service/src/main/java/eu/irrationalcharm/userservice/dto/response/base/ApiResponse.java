package eu.irrationalcharm.userservice.dto.response.base;

import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public class ApiResponse{

    public static <T> ResponseEntity<SuccessResponseDto<T>> success(HttpStatus httpStatus, SuccessfulCode code, String detail, T payload, HttpServletRequest request) {
        return ResponseEntity.status(httpStatus)
                .body(SuccessResponseDto.<T>builder()
                        .status(httpStatus.value())
                        .code(code.name())
                        .detail(detail)
                        .data(payload)
                        .instance(request.getRequestURI())
                        .timestamp(Instant.now())
                        .build());
    }

    public static <T> ResponseEntity<ErrorResponseDto<T>> error(HttpStatus httpStatus, String code, String detail, T payload, HttpServletRequest request) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponseDto.<T>builder()
                        .type("about:blank")
                        .code(code)
                        .status(httpStatus.value())
                        .detail(detail)
                        .error(payload)
                        .instance(request.getRequestURI())
                        .timestamp(Instant.now())
                        .build());
    }
}
