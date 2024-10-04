package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.CourseChangeRequest;
import sesac.server.user.repository.search.CourseChangeRequestSearch;

public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long>,
        CourseChangeRequestSearch {

    boolean existsByStudentIdAndStatusCode(Long studentId, Integer statusCode);

}
