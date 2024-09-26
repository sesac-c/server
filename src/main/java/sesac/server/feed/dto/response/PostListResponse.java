package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Post;

public record PostListResponse(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
        String thumbnail,
        Long likesCount,
        Long replyCount,
        List<String> tags

) {

    public PostListResponse(Post post) {
        this(
                post.getId(),
                post.getUser().getStudent().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getThumbnail(),
                post.getLikesCount(),
                post.getReplyCount(),
                post.getHashtags().stream().map(r -> r.getHashtag().getName()).toList()
        );
    }
}

