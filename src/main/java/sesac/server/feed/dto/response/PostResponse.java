package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Post;

public record PostResponse(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
        List<String> hashtags,
        String imageUrl,
        Long likesCount,
        Long replyCount,
        String profileImage,
        List<ReplyResponse> replies
) {

    public PostResponse(Post post, List<ReplyResponse> replies) {
        this(
                post.getId(),
                post.getUser().getStudent().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getHashtags().stream().map(postHashtag -> postHashtag.getHashtag().getName())
                        .toList(),
                post.getImage(),
                post.getLikesCount(),
                post.getReplyCount(),
                post.getUser().getStudent().getProfileImage(),
                replies
        );
    }
}

