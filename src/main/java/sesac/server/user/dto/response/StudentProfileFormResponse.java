package sesac.server.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record StudentProfileFormResponse(
        String profileImage,
        String nickname,
        Long campusId,
        String campusName,
        Long courseId,
        String courseName
) {

    @QueryProjection
    public StudentProfileFormResponse {
    }

}
