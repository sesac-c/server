package sesac.server.feed.dto;

import java.time.LocalDateTime;

public record PostResponse(
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

}

