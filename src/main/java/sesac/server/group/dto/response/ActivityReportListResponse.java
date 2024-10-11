package sesac.server.group.dto.response;

import java.time.format.DateTimeFormatter;
import sesac.server.group.entity.ActivityReport;

public record ActivityReportListResponse(
        Long id,
        String photo,
        String title
) {

    public static ActivityReportListResponse from(ActivityReport activityReport) {
        String title = activityReport.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 보고서"));

        return new ActivityReportListResponse(
                activityReport.getId(), activityReport.getPhoto(), title);
    }
}
