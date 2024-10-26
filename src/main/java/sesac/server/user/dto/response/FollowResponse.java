package sesac.server.user.dto.response;

import sesac.server.campus.entity.Course;
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

    public static FollowResponse from(Manager manager, Long currentUserId, boolean isFollowing) {
        Long id = manager.getId();
        return new FollowResponse(
                id,
                manager.getCampus().getName() + " 캠퍼스",
                manager.getCampus().getAddress(),
                manager.getProfile(),
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
                student.getProfile(),
                isFollowing,
                id == currentUserId
        );
    }
}
