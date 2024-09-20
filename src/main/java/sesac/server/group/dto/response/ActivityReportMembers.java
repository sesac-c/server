package sesac.server.group.dto.response;

import sesac.server.group.entity.RunningMateMember;

public record ActivityReportMembers(
        String name,
        String role,
        String phone
) {

    private ActivityReportMembers(RunningMateMember runningMateMember) {
        this(
                runningMateMember.getUser().getStudent().getName(),
                runningMateMember.getRole().name(),
                runningMateMember.getPhoneNumber()
        );
    }

    public static ActivityReportMembers from(RunningMateMember runningMateMember) {
        return new ActivityReportMembers(runningMateMember);
    }
}
