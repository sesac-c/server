package sesac.server.auth.exception;

import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.ErrorCode;

public class AccountException extends BaseException {

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
