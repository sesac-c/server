package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.CourseChangeRequest;

public interface CourseChangeRequestRepository extends JpaRepository<CourseChangeRequest, Long> {

    boolean existsByStudentIdAndStatusCode(Long studentId, Integer statusCode);

}
