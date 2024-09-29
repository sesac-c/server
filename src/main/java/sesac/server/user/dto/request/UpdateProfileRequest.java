package sesac.server.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Pattern(regexp = "^[가-힣0-9\\s]+$", message = "INVALID_NICKNAME")
        @Size(min = 1, max = 10, message = "INVALID_NICKNAME_SIZE")
        @Pattern(regexp = "^(?!.*[0-9]+$).*$", message = "INVALID_NICKNAME_COMBINATION")
        String nickname,
        String profileImage,
        Boolean removed
) {

}
