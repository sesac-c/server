package sesac.server.feed.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
//        int likeCount,
//        int replyCount,
//        String[] hashTags,
        String imageUrl
//        String profileImage,
//        Reply[] replies

) {

    @QueryProjection
    public PostResponse {
    }
}

