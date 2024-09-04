package sesac.server.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    UNACCEPT(HttpStatus.UNAUTHORIZED, "Token is null or too short"),
    BADTYPE(HttpStatus.UNAUTHORIZED, "Token type Bearer"),
    MALFORM(HttpStatus.FORBIDDEN, "Malformed Token"),
    BADSIGN(HttpStatus.FORBIDDEN, "BadSignatured Token"),
    EXPIRED(HttpStatus.FORBIDDEN, "Expired Token");;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
