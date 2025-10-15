package eu.irrationalcharm.messagepersistenceservice.exception;


import eu.irrationalcharm.messagepersistenceservice.dto.response.ApiResponse;
import eu.irrationalcharm.messagepersistenceservice.dto.response.ErrorResponseDto;
import eu.irrationalcharm.messagepersistenceservice.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();

        validationErrorList.forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String validationMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.<Map<String, String>>builder()
                        .type("about:blank")
                        .code(ErrorCode.VALIDATION_ERROR.toString())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail("One or more fields have an error")
                        .error(validationErrors)
                        .instance(request.getDescription(false))
                        .timestamp(Instant.now())
                        .build());
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponseDto<Object>> businessException(BusinessException e, HttpServletRequest request) {
        return ApiResponse.error(
                e.getHttpStatus(),
                e.getErrorCode().toString(),
                e.getMessage(),
                null,
                request);
    }
}
