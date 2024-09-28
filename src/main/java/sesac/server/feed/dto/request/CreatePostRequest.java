package sesac.server.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;
import sesac.server.user.entity.User;

public record CreatePostRequest(
        @NotBlank(message = "REQUIRED_TITLE")
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        List<String> hashtag,

        String image,
        String thumbnail
) {

    public CreatePostRequest {
        if (hashtag == null) {
            hashtag = new ArrayList<>();
        }
    }

    public Post toEntity(User user, PostType postType) {
        return Post.builder()
                .type(postType)
                .title(title)
                .content(content)
                .image(image)
                .thumbnail(thumbnail)
                .user(user)
                .build();
    }
}