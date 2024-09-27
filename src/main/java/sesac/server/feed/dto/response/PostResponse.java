package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Post;

public record PostResponse(
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
        String profileImage
) {

    public PostResponse(Post post, boolean likesStatus) {
        this(
                post.getId(),
                post.getUser().getStudent().getNickname(),
                post.getUser().getStudent().getFirstCourse().getCampus().getName(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getHashtags().stream().map(postHashtag -> postHashtag.getHashtag().getName())
                        .toList(),
                post.getImage(),
                likesStatus,
                post.getLikesCount(),
                post.getUser().getStudent().getProfileImage()
        );
    }
}