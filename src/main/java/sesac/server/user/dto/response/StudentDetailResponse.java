package sesac.server.user.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.user.entity.Student;

public record StudentDetailResponse(
        String name,
        String nickname,
        Character gender,
        LocalDate birthDate,
        String email,
        Campus campus,
        Course course,
        LocalDateTime createdAt
) {

    private StudentDetailResponse(Student student) {
        this(
                student.getName(),
                student.getNickname(),
                student.getGender(),
                student.getBirthDate(),
                student.getUser().getEmail(),
                student.getFirstCourse().getCampus(),
                student.getFirstCourse(),
                student.getUser().getCreatedAt()
        );
    }

    public static StudentDetailResponse from(Student student) {

        return new StudentDetailResponse(student);
    }
}
