package sesac.server.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailCheckRequest(
        @NotBlank(message = "REQUIRED_EMAIL")
        @Email(message = "INVALID_EMAIL_PATTERN")
        String email
) {

}
