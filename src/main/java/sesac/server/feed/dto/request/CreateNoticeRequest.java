package sesac.server.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import sesac.server.campus.entity.Course;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.NoticeType;
import sesac.server.user.entity.User;

public record CreateNoticeRequest(
        @NotBlank(message = "REQUIRED_TITLE")
        @Size(min = 1, max = 20, message = "INVALID_TITLE_SIZE")
        String title,

        @NotBlank(message = "REQUIRED_CONTENT")
        @Size(min = 1, max = 500, message = "INVALID_CONTENT_SIZE")
        String content,

        String image,

        List<String> hashtags,

        Integer importance,

        Long courseId
) {

    public CreateNoticeRequest {
        if (hashtags == null) {
            hashtags = new ArrayList<>();
        }

        if (importance == null) {
            importance = 0;
        }
    }

    public Notice toEntity(User user, NoticeType noticeType, Course course) {
        return Notice.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(noticeType)
                .importance(importance)
                .image(image)
                .status(true)
                .course(course)
                .build();
    }
}
