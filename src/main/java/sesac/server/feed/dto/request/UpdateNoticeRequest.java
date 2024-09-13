package sesac.server.feed.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateNoticeRequest(
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        Integer importance

//        String[] hashtag,
//
//        String imageUrl
) {

}