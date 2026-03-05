package eu.irrationalcharm.mediaservice.exception;

import eu.irrationalcharm.dto.response.ErrorResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.mediaservice.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto<Void>> handleMaxSizeException(HttpServletRequest request) {
        log.warn("Upload failed: File size exceeded the configured maximum limit. Path: {}", request.getRequestURI());

        return ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                ErrorCode.FILE_TOO_LARGE.name(),
                "File size exceeds the configured maximum limit",
                null,
                request
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto<Object>> businessException(BusinessException businessException, HttpServletRequest request) {

        return ApiResponse.error(
                businessException.getHttpStatus(),
                businessException.getErrorCode().toString(),
                businessException.getMessage(),
                null,
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto<Object>> internalException(Exception exception, HttpServletRequest request) {

        log.error("An unexpected internal server error occurred", exception);

        return ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong",
                "An unexpected error occurred. Please contact support.",
                null,
                request);
    }
}
