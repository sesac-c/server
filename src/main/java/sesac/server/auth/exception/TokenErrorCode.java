package sesac.server.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    UNACCEPT(HttpStatus.UNAUTHORIZED, "UNACCEPT", "Token is null or too short"),
    BADTYPE(HttpStatus.UNAUTHORIZED, "BADTYPE", "Token type Bearer"),
    MALFORM(HttpStatus.FORBIDDEN, "MALFORM", "Malformed Token"),
    BADSIGN(HttpStatus.FORBIDDEN, "BADSIGN", "BadSignatured Token"),
    EXPIRED(HttpStatus.FORBIDDEN, "EXPIRED", "Expired Token");;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
