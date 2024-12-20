package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import sesac.server.feed.entity.Reply;
import sesac.server.user.entity.User;

public record ReplyResponse(
        Long id,
        Long writerId,
        String writer,
        String content,
        LocalDateTime createdAt,
        String profileImage,
        boolean isReplyMine
) {

    private ReplyResponse(Long currentUserId, Reply reply, User user, String nickname,
            String profileImage) {
        this(
                reply.getId(),
                user.getId(),
                nickname,
                reply.getContent(),
                reply.getCreatedAt(),
                profileImage,
                currentUserId == user.getId()
        );
    }

    private static String getNicknameFromEntity(User user) {
        return switch (user.getRole()) {
            case MANAGER -> user.getManager().getCampus().getName() + " 캠퍼스";
            case STUDENT -> user.getStudent().getNickname();
            default -> null;
        };
    }

    private static String getProfileImageFromEntity(User user) {
        return switch (user.getRole()) {
            case MANAGER -> user.getManager().getProfile();
            case STUDENT -> user.getStudent().getProfile();
            default -> null;
        };
    }

    public static ReplyResponse from(Reply reply, Long currentUserId) {
        User user = reply.getUser();
        return new ReplyResponse(currentUserId, reply, user, getNicknameFromEntity(user),
                getProfileImageFromEntity(user));
    }
}
