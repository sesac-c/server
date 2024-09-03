package sesac.server.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "REQUIRED_EMAIL")
        @Email(message = "INVALID_EMAIL_PATTERN")
        String email,

        @NotBlank(message = "REQUIRED_PASSWORD")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,20}$",
                message = "INVALID_PASSWORD_PATTERN"
        )
        String password,

        @NotBlank(message = "REQUIRED_PASSWORD_CONFIRM")
        String passwordConfirm,

        @NotBlank(message = "REQUIRED_NAME")
        @Pattern(regexp = "^[가-힣]+$", message = "INVALID_NAME_PATTERN")
        @Size(min = 1, max = 5, message = "INVALID_NAME_SIZE")
        String name,

        @NotNull(message = "REQUIRED_BIRTH")
        @Pattern(regexp = "^\\d+$", message = "INVALID_BIRTH_PATTERN")
        @Size(min = 6, max = 6, message = "INVALID_BIRTH_SIZE")
        String birthDate,

        @NotNull(message = "REQUIRED_GENDER")
        @Min(value = 1, message = "INVALID_GENDER")
        @Max(value = 4, message = "INVALID_GENDER")
        int gender,

        @NotNull(message = "REQUIRED_COURSE")
        Long firstCourseId
) {

}
