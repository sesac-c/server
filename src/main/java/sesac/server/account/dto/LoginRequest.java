package sesac.server.account.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "REQUIRED_EMAIL")
        String email,

        @NotBlank(message = "REQUIRED_PASSWORD")
        String password
) {

}