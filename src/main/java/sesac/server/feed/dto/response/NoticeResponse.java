package sesac.server.feed.dto.response;

import static org.springframework.util.StringUtils.hasText;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.common.constants.AppConstants;
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

    private NoticeResponse(Long currentUserId, Notice notice, User user, String profileImage,
            String campusName,
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
                profileImage,
                user.getId(),
                currentUserId == user.getId()
        );
    }

    private static String getProfile(User user) {
        String profileImage = user.getManager().getProfileImage();
        return hasText(profileImage) ? profileImage : AppConstants.DEFAULT_PROFILE_IMAGE;
    }

    public static NoticeResponse from(Long currentUserId, Notice notice, boolean likesStatus) {
        User user = notice.getUser();
        String campusName = user.getManager().getCampus().getName();
        return new NoticeResponse(
                currentUserId,
                notice,
                user,
                getProfile(user),
                campusName,
                likesStatus
        );
    }
}