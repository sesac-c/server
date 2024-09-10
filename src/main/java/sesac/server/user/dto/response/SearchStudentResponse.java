package sesac.server.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import sesac.server.user.entity.Student;

public record SearchStudentResponse(
        Long id,
        String name,
        String email,
        String campus,
        String course,
        Integer status,
        LocalDateTime createdAt
) {

    private SearchStudentResponse(Student student) {
        this(
                student.getId(),
                student.getName(),
                student.getUser().getEmail(),
                student.getFirstCourse().getCampus().getName(),
                student.getFirstCourse().getName(),
                student.getStatusCode(),
                student.getUser().getCreatedAt()
        );
    }

    public static SearchStudentResponse from(Student student) {
        return new SearchStudentResponse(student);
    }

    @QueryProjection
    public SearchStudentResponse {

    }
}
