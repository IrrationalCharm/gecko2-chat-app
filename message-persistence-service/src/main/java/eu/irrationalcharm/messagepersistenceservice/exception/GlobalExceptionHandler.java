package eu.irrationalcharm.messagepersistenceservice.exception;


import eu.irrationalcharm.dto.response.ErrorResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.messagepersistenceservice.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
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


    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        List<ParameterValidationResult> validationErrorList = ex.getParameterValidationResults();

        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String parameterName = result.getMethodParameter().getParameterName();

            String errorMessage = result.getResolvableErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .findFirst()
                    .orElse(result.getResolvableErrors().getFirst().toString()); // Fallback

            errors.put(parameterName, errorMessage);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto.<Map<String, String>>builder()
                        .type("about:blank")
                        .code(ErrorCode.VALIDATION_ERROR.toString())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail("One or more fields have an error")
                        .error(errors)
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
