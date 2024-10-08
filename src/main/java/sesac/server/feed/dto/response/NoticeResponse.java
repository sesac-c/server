package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Notice;

public record NoticeResponse(
        Long id,
        String nickname,
        String title,
        String content,
        LocalDateTime createdAt,
        List<String> hashtags,
        String imageUrl,
        Long likesCount,
        boolean likesStatus,
        String profileImage
//        List<ReplyResponse> replies
) {

    public NoticeResponse(Notice notice, boolean likesStatus) {
        this(
                notice.getId(),
                notice.getUser().getManager().getCampus().getName(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getHashtags().stream().map(postHashtag -> postHashtag.getHashtag().getName())
                        .toList(),
                notice.getImage(),
                notice.getLikesCount(),
                likesStatus,
                notice.getUser().getManager().getProfileImage()
//                replies
        );
    }
}
