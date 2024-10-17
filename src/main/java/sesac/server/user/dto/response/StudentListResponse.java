package sesac.server.user.dto.response;

import static org.springframework.util.StringUtils.hasText;

import sesac.server.common.constants.AppConstants;
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
                hasText(student.getProfileImage()) ? student.getProfileImage()
                        : AppConstants.DEFAULT_PROFILE_IMAGE
        );
    }
}
