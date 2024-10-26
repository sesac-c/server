package sesac.server.feed.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sesac.server.feed.entity.Post;
import sesac.server.user.entity.User;

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
        String profileImage,
        Long userId,
        boolean isPostMine
) {

    private PostResponse(Long currentUserId, Post post, User user, boolean likesStatus) {
        this(
                post.getId(),
                user.getStudent().getNickname(),
                user.getStudent().getFirstCourse().getCampus().getName(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getHashtags().stream().map(postHashtag -> postHashtag.getHashtag().getName())
                        .toList(),
                post.getImage(),
                likesStatus,
                post.getLikesCount(),
                user.getStudent().getProfile(),
                user.getId(),
                currentUserId == user.getId()
        );
    }


    public static PostResponse from(Long currentUserId, Post post, boolean likesStatus) {
        User user = post.getUser();
        return new PostResponse(currentUserId, post, user, likesStatus);
    }
}