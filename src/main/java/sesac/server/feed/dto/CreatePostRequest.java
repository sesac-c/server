package sesac.server.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "REQUIRED_TITLE")
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        String[] hashtag,

        String imageUrl
) {

}