package sesac.server.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record CourseChangeRequestRequest(
        @NotNull(message = "NO_COURSE_CHANGE_REQUEST_STATUS")
        int status,
        String rejectReason
) {

}