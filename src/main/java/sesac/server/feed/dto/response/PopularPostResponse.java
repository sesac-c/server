package sesac.server.feed.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record PopularPostResponse(
        Long id,
        String title
) {

    @QueryProjection
    public PopularPostResponse {
    }
}
