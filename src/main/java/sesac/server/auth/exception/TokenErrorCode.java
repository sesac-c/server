package sesac.server.auth.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    TOKEN_UNACCEPT(UNAUTHORIZED, "토큰이 없거나 너무 짧습니다"),
    TOKEN_MALFORM(FORBIDDEN, "잘못된 토큰입니다"),
    ACCESS_TOKEN_EXPIRED(FORBIDDEN, "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(FORBIDDEN, "리프레시 토큰이 만료되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
