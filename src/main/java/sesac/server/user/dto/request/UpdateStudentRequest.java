package sesac.server.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;

public record UpdateStudentRequest(
        @Pattern(regexp = "^[가-힣]+$", message = "INVALID_NAME_PATTERN")
        @Size(min = 1, max = 5, message = "INVALID_NAME_SIZE")
        String name,

        @Size(min = 1, max = 10, message = "INVALID_NICKNAME_SIZE")
        String nickname,

        Campus campus,
        Course course

) {

}
