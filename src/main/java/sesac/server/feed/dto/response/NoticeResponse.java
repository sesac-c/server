package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Notice;
import sesac.server.user.entity.User;

public record NoticeResponse(
        Long id,
        String nickname,
        String campusName,
        String title,
        String content,
        LocalDateTime createdAt,
        List<String> hashtags,
        String imageUrl,
        boolean likesStatus,
        Long likesCount,
        String profileImage,
        Long userId,
        boolean isNoticeMine
) {

    private NoticeResponse(Long currentUserId, Notice notice, User user, String campusName,
            boolean likesStatus) {
        this(
                notice.getId(),
                campusName + " 캠퍼스",
                campusName,
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getHashtags().stream()
                        .map(noticeHashtag -> noticeHashtag.getHashtag().getName()).toList(),
                notice.getImage(),
                likesStatus,
                notice.getLikesCount(),
                notice.getUser().getManager().getProfile(),
                user.getId(),
                currentUserId == user.getId()
        );
    }

    public static NoticeResponse from(Long currentUserId, Notice notice, boolean likesStatus) {
        User user = notice.getUser();
        String campusName = user.getManager().getCampus().getName();
        return new NoticeResponse(
                currentUserId,
                notice,
                user,
                campusName,
                likesStatus
        );
    }
}