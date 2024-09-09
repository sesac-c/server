package sesac.server.user.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NO_USER(NOT_FOUND, "회원이 존재하지 않습니다"),
    NO_MANAGER(NOT_FOUND, "매니저가 존재하지 않습니다"),
    INVALID_NICKNAME_SIZE(BAD_REQUEST, "닉네임은 1 ~ 10자로 입력해주세요");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}