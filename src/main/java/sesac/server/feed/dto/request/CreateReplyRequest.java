package sesac.server.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReplyRequest(
        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 100, message = "INVALID_REPLY_CONTENT_SIZE")
        String content
) {

}