package sesac.server.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.user.entity.User;

public record CreateRunningMateMemberRequest(
        @NotNull(message = "REQUIRED_USER")
        Long userId,

        @NotNull(message = "REQUIRED_ROLE")
        RunningMateMember.MemberRole role,

        @NotBlank(message = "REQUIRED_PHONE")
        @Size(max = 20, message = "INVALID_PHONE")
        String phoneNumber

) {

    public RunningMateMember toEntity(User user, RunningMate runningMate) {
        return RunningMateMember.builder()
                .runningMate(runningMate)
                .user(user)
                .role(role)
                .phoneNumber(phoneNumber)
                .build();
    }
}
