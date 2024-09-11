package sesac.server.group.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SearchRunningMateResponse(
        Long id,
        String name,
        String subject,
        String goal,
        String course
) {

    @QueryProjection
    public SearchRunningMateResponse {

    }
}
