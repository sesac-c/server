package sesac.server.user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.user.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.firstCourse WHERE s.nickname LIKE LOWER(CONCAT('%', :nickname, '%'))")
    List<Student> findByNicknameContainingIgnoreCase(@Param("nickname") String nickname);

}
