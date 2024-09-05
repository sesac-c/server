package sesac.server.user.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NO_USER(NOT_FOUND, "회원이 존재하지 않습니다");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}