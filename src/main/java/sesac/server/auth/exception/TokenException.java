package sesac.server.auth.exception;

import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.ErrorCode;

public class TokenException extends BaseException {
    
    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

}
