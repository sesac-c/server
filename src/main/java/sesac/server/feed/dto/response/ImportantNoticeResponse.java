package sesac.server.feed.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ImportantNoticeResponse(
        Long id,
        String title,
        String campus
) {

    @QueryProjection
    public ImportantNoticeResponse {

    }

}
