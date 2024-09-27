package sesac.server.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NicknameCheckRequest(
        @NotBlank(message = "REQUIRED_NICKNAME")
        String nickname
) {

}
