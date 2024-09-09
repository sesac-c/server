package sesac.server.campus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateCourseRequest(
        @NotBlank(message = "REQUIRED_NAME")
        @Size(min = 1, max = 50, message = "INVALID_NAME_SIZE")
        String name,

        @NotBlank(message = "REQUIRED_CLASS_NUMBER")
        @Size(min = 1, max = 50, message = "INVALID_CLASS_NUMBER_SIZE")
        String classNumber,

        @NotBlank(message = "REQUIRED_INSTRUCTOR_NAME")
        @Pattern(regexp = "^[가-힣]+$", message = "INVALID_INSTRUCTOR_NAME_PATTERN")
        @Size(min = 1, max = 5, message = "INVALID_INSTRUCTOR_NAME_SIZE")
        String instructorName,

        @NotNull(message = "REQUIRED_START_DATE")
        LocalDate startDate,               // "2024-09-01" 형식으로 전달되면 자동 전환, 불일치 시 500 에러

        @NotNull(message = "REQUIRED_END_DATE")
        LocalDate endDate
) {

}
