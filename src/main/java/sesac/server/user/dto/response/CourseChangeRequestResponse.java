package sesac.server.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record CourseChangeRequestResponse(
        Long id,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        String campusName,
        int statusCode,
        LocalDateTime createdAt
) {

    @QueryProjection
    public CourseChangeRequestResponse {
    }

}
