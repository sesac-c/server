package sesac.server.group.dto.response;

import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.entity.RunningMateMember.MemberRole;

public record RunningMateMemberDetailResponse(
        Long userId,
        String userName,
        Long runningMateId,
        String runningMateName,
        MemberRole role,
        String phoneNumber

) {

    private RunningMateMemberDetailResponse(RunningMateMember runningMateMember) {
        this(
                runningMateMember.getUser().getId(),
                runningMateMember.getUser().getStudent().getName(),
                runningMateMember.getRunningMate().getId(),
                runningMateMember.getRunningMate().getName(),
                runningMateMember.getRole(),
                runningMateMember.getPhoneNumber()
        );
    }

    public static RunningMateMemberDetailResponse from(RunningMateMember runningMateMember) {
        return new RunningMateMemberDetailResponse(runningMateMember);
    }
}
