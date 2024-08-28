package sesac.server.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        Map<String, String> errorMap = new HashMap<>();

        if (e.hasErrors()) {
            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
        }
        String errorMapString = errorMap.toString();

        log.error("입력 에러 : {}", errorMapString);

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMapString));
    }

    // 사용자 정의 예외
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        int statusValue = errorCode.getStatus().value();

        ErrorResponse errorResponse =
                new ErrorResponse(statusValue, errorCode.getMessage());

        log.error("사용자 정의 에러 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(statusValue)
                .body(errorResponse);
    }

    // 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        int statusValue = HttpStatus.INTERNAL_SERVER_ERROR.value();

        ErrorResponse errorResponse =
                new ErrorResponse(statusValue, GlobalErrorCode.INTERNAL_SERVER_ERROR.getMessage());

        log.error("서버 에러 : {}", e.getMessage(), e);
        return ResponseEntity
                .status(statusValue)
                .body(errorResponse);
    }
}
