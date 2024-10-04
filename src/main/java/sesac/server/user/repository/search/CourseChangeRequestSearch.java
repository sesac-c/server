package sesac.server.user.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.user.dto.response.CourseChangeRequestResponse;

public interface CourseChangeRequestSearch {

    Page<CourseChangeRequestResponse> searchCourseChangeRequests(
            Pageable pageable, String status
    );
}
