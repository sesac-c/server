package sesac.server.campus.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import sesac.server.campus.entity.Course;

public record CourseDetailResponse(
        Long id,
        Long campusId,
        String name,
        String classNumber,
        String instructorName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {

    private CourseDetailResponse(Course course) {
        this(
                course.getId(),
                course.getCampus().getId(),
                course.getName(),
                course.getClassNumber(),
                course.getInstructorName(),
                course.getStartDate(),
                course.getEndDate(),
                course.getCreatedAt()
        );
    }

    public static CourseDetailResponse from(Course course) {
        return new CourseDetailResponse(course);
    }
}
