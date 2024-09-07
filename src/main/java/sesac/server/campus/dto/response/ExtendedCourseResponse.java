package sesac.server.campus.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import sesac.server.campus.entity.Course;

public record ExtendedCourseResponse(
        Long id,
        String name,
        String classNumber,
        String instructorName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {

    private ExtendedCourseResponse(Course course) {
        this(
                course.getId(),
                course.getName(),
                course.getClassNumber(),
                course.getInstructorName(),
                course.getStartDate(),
                course.getEndDate(),
                course.getCreatedAt()
        );
    }

    public static ExtendedCourseResponse from(Course course) {
        return new ExtendedCourseResponse(course);
    }
}
