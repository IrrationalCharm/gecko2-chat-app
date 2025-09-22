package eu.irrationalcharm.userservice.exception;

import eu.irrationalcharm.userservice.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public BusinessException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
