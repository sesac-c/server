package sesac.server.campus.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateCourseRequest(
        @Size(min = 1, max = 50, message = "INVALID_NAME_SIZE")
        String name,

        @Size(min = 1, max = 50, message = "INVALID_CLASS_NUMBER_SIZE")
        String classNumber,

        @Pattern(regexp = "^[가-힣]+$", message = "INVALID_INSTRUCTOR_NAME_PATTERN")
        @Size(min = 1, max = 5, message = "INVALID_INSTRUCTOR_NAME_SIZE")
        String instructorName,

        LocalDate startDate,

        LocalDate endDate,

        Long newCampusId
) {

}
