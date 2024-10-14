package sesac.server.account.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UuidCheckRequest(
        @NotBlank(message = "REQUIRED_UUID")
        String uuid
) {

}
