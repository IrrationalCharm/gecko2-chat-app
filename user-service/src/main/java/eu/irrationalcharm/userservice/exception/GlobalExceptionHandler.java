package eu.irrationalcharm.userservice.exception;

import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.ErrorResponseDto;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {



    /**
     * Handles Error response for hibernate validation errors.
     * @param ex the exception to handle
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatusCode status, WebRequest request) {
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
    public ResponseEntity<ErrorResponseDto<Object>> businessException(BusinessException businessException, HttpServletRequest request) {

        return ApiResponse.error(businessException.getHttpStatus(),
                businessException.getErrorCode().toString(),
                businessException.getMessage(),
                null,
                request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto<Object>> internalException(Exception exception, HttpServletRequest request) {

        log.error("An unexpected internal server error occurred", exception);

        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                "An unexpected error occurred. Please contact support.",
                null,
                request);
    }

}
