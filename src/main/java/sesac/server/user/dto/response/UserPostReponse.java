package sesac.server.user.dto.response;

import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;

public record UserPostReponse(
        Long id,
        PostType postType,
        String title,
        String content,
        String image
) {

    private UserPostReponse(Post post) {
        this(post.getId(), post.getType(), post.getTitle(), post.getContent(), post.getImage());
    }

    public static UserPostReponse from(Post post) {
        return new UserPostReponse(post);
    }
}
