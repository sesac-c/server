package sesac.server.feed.dto;

import java.time.LocalDateTime;
import sesac.server.feed.entity.Reply;

public record ReplyResponse(
        Long id,
        String writer,
        String content,
        LocalDateTime createdAt,
        String profileImage
) {

    public ReplyResponse(Reply reply) {
        this(
                reply.getId(),
                reply.getUser().getStudent().getNickname(),
                reply.getContent(),
                reply.getCreatedAt(),
                reply.getUser().getStudent().getProfileImage()
        );
    }
}
