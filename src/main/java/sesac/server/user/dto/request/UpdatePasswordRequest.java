package sesac.server.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePasswordRequest(
        @NotBlank(message = "REQUIRED_PASSWORD")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
                message = "INVALID_PASSWORD_PATTERN"
        )
        String password,

        @NotBlank(message = "REQUIRED_PASSWORD_CONFIRM")
        String passwordConfirm) {

}
