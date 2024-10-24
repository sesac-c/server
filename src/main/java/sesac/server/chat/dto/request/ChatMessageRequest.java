package sesac.server.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull(message = "REQUIRED_CONTENT")
        String content
) {

}