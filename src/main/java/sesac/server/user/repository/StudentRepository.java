package sesac.server.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.user.entity.Student;
import sesac.server.user.repository.search.StudentSearch;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentSearch {

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.firstCourse WHERE s.nickname LIKE LOWER(CONCAT('%', :nickname, '%'))")
    List<Student> findByNicknameContainingIgnoreCase(@Param("nickname") String nickname);


    @EntityGraph(attributePaths = {"user", "firstCourse", "firstCourse.campus"})
    Optional<Student> findById(Long id);

    boolean existsByNickname(String nickname);

}
