package sesac.server.feed.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
//        String[] hashTags,
        String imageUrl,
        Long likesCount,
        Long replyCount
//        String profileImage,
//        Reply[] replies
) {

}

