package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
