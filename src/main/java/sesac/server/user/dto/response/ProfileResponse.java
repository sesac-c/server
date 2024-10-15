package sesac.server.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record ProfileResponse(
        String nickname,
        String affiliation,
        String profileImage,
        Boolean isProfileMine,
        Long followCount,
        Long followerCount,
        Boolean isFollowing
) {

    @QueryProjection
    public ProfileResponse {
    }
}