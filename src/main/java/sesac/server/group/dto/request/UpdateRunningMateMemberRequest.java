package sesac.server.group.dto.request;

import jakarta.validation.constraints.Size;
import sesac.server.group.entity.RunningMateMember;

public record UpdateRunningMateMemberRequest(
        RunningMateMember.MemberRole role,

        @Size(max = 20, message = "INVALID_PHONE")
        String phoneNumber

) {

}
