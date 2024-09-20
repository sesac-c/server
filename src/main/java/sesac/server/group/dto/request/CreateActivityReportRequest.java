package sesac.server.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sesac.server.common.exception.BaseException;
import sesac.server.group.entity.ActivityReport;
import sesac.server.group.exception.RunningMateErrorCode;

public record CreateActivityReportRequest(
        @NotNull(message = "REQUIRED_DURATION")
        Integer duration,

        @NotBlank(message = "REQUIRED_MAIN_CONTENT")
        String mainContent,

        @NotBlank(message = "REQUIRED_ACHIEVEMENT_SUMMARY")
        String achievementSummary,

        @NotBlank(message = "REQUIRED_PHOTO")
        String photo,

        List<Long> memberIds
) {

    public CreateActivityReportRequest {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new BaseException(RunningMateErrorCode.REQUIRED_PARTICIPANT);
        }
    }

    public ActivityReport toEntity() {
        return ActivityReport.builder()
                .activityDuration(duration)
                .mainContent(mainContent)
                .achievementSummary(achievementSummary)
                .photo(photo)
                .statusCode(0)
                .build();
    }
}
