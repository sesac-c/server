package sesac.server.user.dto.response;

import static org.springframework.util.StringUtils.hasText;

import sesac.server.campus.entity.Course;
import sesac.server.common.constants.AppConstants;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;

public record FollowResponse(
        Long id,
        String nickname,
        String description,
        String profileImage,
        boolean isFollowing,
        boolean isThisMe
) {

    private static String getProfileImage(String profileImage) {
        return hasText(profileImage) ? profileImage : AppConstants.DEFAULT_PROFILE_IMAGE;
    }

    public static FollowResponse from(Manager manager, Long currentUserId, boolean isFollowing) {
        Long id = manager.getId();
        return new FollowResponse(
                id,
                manager.getCampus().getName() + " 캠퍼스",
                manager.getCampus().getAddress(),
                getProfileImage(manager.getProfileImage()),
                isFollowing,
                id == currentUserId
        );
    }

    public static FollowResponse from(Student student, Long currentUserId, boolean isFollowing) {
        Long id = student.getId();
        Course course = student.getCourse();
        return new FollowResponse(
                id,
                student.getNickname(),
                String.format("(%s기) %s", course.getClassNumber(), course.getName()),
                getProfileImage(student.getProfileImage()),
                isFollowing,
                id == currentUserId
        );
    }
}
