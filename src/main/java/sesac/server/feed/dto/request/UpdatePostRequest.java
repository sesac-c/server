package sesac.server.feed.dto.request;

import jakarta.validation.constraints.Size;

public record UpdatePostRequest(
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content

//        String[] hashtag,
//
//        String imageUrl
) {

}