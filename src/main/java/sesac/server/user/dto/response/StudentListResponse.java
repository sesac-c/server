package sesac.server.user.dto.response;

import sesac.server.user.entity.Student;

public record StudentListResponse(
        Long id,
        String nickname,
        String courseName,
        String profileImage
) {

    public StudentListResponse(Student student) {
        this(
                student.getId(),
                student.getNickname(),
                student.getFirstCourse().getName(),
                student.getProfileImage()
        );
    }
}
