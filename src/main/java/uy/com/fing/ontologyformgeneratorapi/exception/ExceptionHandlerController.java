package uy.com.fing.ontologyformgeneratorapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(ApiException.class)
    private ResponseEntity<ApiError> handleApiException(ApiException e) {
        return handleException(e, e.getStatus(), e.getStatus() >= 500 ? ErrorLevel.ERROR : ErrorLevel.WARN);
    }

    private ResponseEntity<ApiError> handleException(Exception e, int status, ErrorLevel errorLevel) {
        if (errorLevel == ErrorLevel.ERROR) {
            log.error(e.getMessage(), e);
        } else {
            log.warn(e.getMessage());
        }

        return ResponseEntity.status(status)
                .body(new ApiError(e.getMessage()));
    }

    record ApiError(String message){};
    enum ErrorLevel {ERROR, WARN}
}
