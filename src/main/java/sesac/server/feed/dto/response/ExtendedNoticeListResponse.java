package sesac.server.feed.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import sesac.server.feed.entity.NoticeType;

public record ExtendedNoticeListResponse(
        Long id,
        String writer,
        String title,
        String content,
        NoticeType noticeType,
        LocalDateTime createdAt
) {

    @QueryProjection
    public ExtendedNoticeListResponse {

    }
}

