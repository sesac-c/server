package sesac.server.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import sesac.server.feed.entity.NoticeType;

public record CreateNoticeRequest(
        @NotBlank(message = "REQUIRED_TITLE")
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        String image,

        List<String> hashtags,

        @NotNull(message = "REQUIRED_NOTICE_TYPE")
        NoticeType type,

        Integer importance
) {

    public CreateNoticeRequest {
        if (hashtags == null) {
            hashtags = new ArrayList<>();
        }

        if (importance == null) {
            importance = 0;
        }
    }
}
