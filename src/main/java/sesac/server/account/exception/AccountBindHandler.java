package sesac.server.account.exception;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class AccountBindHandler {

    public void signupRequest(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return;
        }

        Set<String> errorMessages = new HashSet<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMessages.add(fieldError.getDefaultMessage());
        });

        if (errorMessages.contains(AccountErrorCode.REQUIRED_NAME.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_NAME);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_NAME_PATTERN.getCode())) {
            throw new AccountException(AccountErrorCode.INVALID_NAME_PATTERN);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_NAME_SIZE.getCode())) {
            throw new AccountException(AccountErrorCode.INVALID_NAME_SIZE);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_BIRTH.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_BIRTH);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_BIRTH.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_BIRTH);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_BIRTH_SIZE.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_BIRTH);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_GENDER.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_GENDER);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_GENDER.getCode())) {
            throw new AccountException(AccountErrorCode.INVALID_GENDER);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_EMAIL.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_EMAIL);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_EMAIL_PATTERN.getCode())) {
            throw new AccountException(AccountErrorCode.INVALID_EMAIL_PATTERN);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_PASSWORD.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_PASSWORD);
        }

        if (errorMessages.contains(AccountErrorCode.INVALID_PASSWORD_PATTERN.getCode())) {
            throw new AccountException(AccountErrorCode.INVALID_PASSWORD_PATTERN);
        }

        if (errorMessages.contains(AccountErrorCode.REQUIRED_PASSWORD_CONFIRM.getCode())) {
            throw new AccountException(AccountErrorCode.REQUIRED_PASSWORD_CONFIRM);
        }

    }
}
