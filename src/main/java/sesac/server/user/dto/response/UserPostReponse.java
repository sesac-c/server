package sesac.server.user.dto.response;

import sesac.server.feed.entity.Post;

public record UserPostReponse(
        Long id,
        String title,
        String content,
        String image
) {

    private UserPostReponse(Post post) {
        this(post.getId(), post.getTitle(), post.getContent(), post.getImage());
    }

    public static UserPostReponse from(Post post) {
        return new UserPostReponse(post);
    }
}
