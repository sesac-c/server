package sesac.server.campus.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.search.CourseSearch;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseSearch {

    public List<Course> findByCampusId(Long campusId);
}
