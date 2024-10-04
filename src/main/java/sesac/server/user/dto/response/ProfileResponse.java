package sesac.server.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileResponse(
        String affiliation, // 소속-학생: 강의, 매니저: 캠퍼스
        String profileImage,
        Boolean isProfileMine,
        Long followCount,
        Long followerCount
) {

    @QueryProjection
    public ProfileResponse {

    }

}
