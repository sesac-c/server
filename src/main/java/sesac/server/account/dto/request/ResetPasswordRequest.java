package sesac.server.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "REQUIRED_UUID")
        String uuid,
        @NotBlank(message = "REQUIRED_PASSWORD")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
                message = "INVALID_PASSWORD_PATTERN"
        )
        String password,
        @NotBlank(message = "REQUIRED_PASSWORD_CONFIRM")
        String passwordConfirm) {

}
