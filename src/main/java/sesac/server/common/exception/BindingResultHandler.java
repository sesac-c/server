package sesac.server.common.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class BindingResultHandler {

    public void handleBindingResult(BindingResult bindingResult, List<ErrorCode> errorCodes) {
        if (!bindingResult.hasErrors()) {
            return;
        }

        Set<String> errorMessages = new HashSet<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMessages.add(fieldError.getDefaultMessage());
        });

        errorCodes.forEach(errorCode -> {
            if (errorMessages.contains(errorCode.getCode())) {
                throw new BaseException(errorCode);
            }
        });
    }
}
