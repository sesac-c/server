package sesac.server.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
        @NotBlank(message = "REQUIRED_TITLE")
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        List<String> hashtag,

        String imageUrl
) {

}