package sesac.server.campus.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.campus.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    public List<Course> findByCampusId(Long campusId);
}
