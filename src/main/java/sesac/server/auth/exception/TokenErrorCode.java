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
    UNACCEPT(UNAUTHORIZED, "Token is null or too short"),
    BADTYPE(UNAUTHORIZED, "Token type Bearer"),
    MALFORM(FORBIDDEN, "Malformed Token"),
    BADSIGN(FORBIDDEN, "BadSignatured Token"),
    EXPIRED(FORBIDDEN, "Expired Token");;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
