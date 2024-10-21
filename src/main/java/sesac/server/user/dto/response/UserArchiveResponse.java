package sesac.server.user.dto.response;

import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;

public record UserArchiveResponse(
        Long id,
        PostType postType,
        String title,
        String content,
        String image
) {

    private UserArchiveResponse(Post post) {
        this(post.getId(), post.getType(), post.getTitle(), post.getContent(), post.getImage());
    }

    public static UserArchiveResponse from(Post post) {
        return new UserArchiveResponse(post);
    }
}
