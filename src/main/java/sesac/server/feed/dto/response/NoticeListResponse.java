package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Notice;

public record NoticeListResponse(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
        String imageUrl,
        Long likesCount,
        Long replyCount,
        List<String> tags

) {

    public NoticeListResponse(Notice notice) {
        this(
                notice.getId(),
                notice.getUser().getManager().getCampus().getName(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getImage(),
                notice.getLikesCount(),
                notice.getReplyCount(),
                notice.getHashtags().stream().map(r -> r.getHashtag().getName()).toList()
        );
    }
}
