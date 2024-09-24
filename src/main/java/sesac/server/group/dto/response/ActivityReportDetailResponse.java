package sesac.server.group.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record ActivityReportDetailResponse(
        String runningMateName,
        String runningMateSubject,
        String runningMateGoal,
        String campusName,
        String courseName,
        int activityDuration,
        LocalDate activityAt,
        String mainContent,
        String achievementSummary,
        String photo,
        LocalDate startDate,
        LocalDate endDate,
        List<ActivityReportMembers> members,
        List<String> participants
) {

}
