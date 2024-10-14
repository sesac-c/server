package sesac.server.feed.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import sesac.server.feed.entity.PostType;

public record PopularPostResponse(
        Long id,
        String text,
        PostType type
) {

    @QueryProjection
    public PopularPostResponse {
    }
}
