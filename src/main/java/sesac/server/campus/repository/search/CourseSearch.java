package sesac.server.campus.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.campus.dto.response.ExtendedCourseResponse;

public interface CourseSearch {

    Page<ExtendedCourseResponse> searchCourse(
            Long managerId,
            Pageable pageable,
            String status
    );
}