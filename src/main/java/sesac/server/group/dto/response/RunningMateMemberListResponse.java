package sesac.server.group.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import sesac.server.group.entity.RunningMateMember.MemberRole;

public record RunningMateMemberListResponse(
        Long userId,
        String userName,
        String courseName,
        String campusName,
        MemberRole role,
        String phoneNumber
) {

    @QueryProjection
    public RunningMateMemberListResponse {

    }
}
