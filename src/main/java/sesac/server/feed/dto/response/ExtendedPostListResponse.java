package sesac.server.feed.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import sesac.server.feed.entity.PostType;

public record ExtendedPostListResponse(
        Long id,
        String writer,
        String title,
        String content,
        PostType postType,
        LocalDateTime createdAt
) {

    @QueryProjection
    public ExtendedPostListResponse {

    }
}

