package sesac.server.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sesac.server.campus.entity.Course;
import sesac.server.group.entity.RunningMate;

public record CreateRunningMateRequest(
        @NotBlank(message = "REQUIRED_NAME")
        String name,
        @NotBlank(message = "REQUIRED_SUBJECT")
        String subject,
        @NotBlank(message = "REQUIRED_GOAL")
        String goal,
        @NotNull(message = "REQUIRED_COURSE")
        Long courseId
) {

    public RunningMate toEntity(Course course) {
        return RunningMate.builder()
                .name(name)
                .subject(subject)
                .goal(goal)
                .course(course)
                .build();
    }
}
