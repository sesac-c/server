package sesac.server.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.chat.entity.CourseChatRoom;

public interface CourseChatRoomRepository extends JpaRepository<CourseChatRoom, Long> {

    Optional<CourseChatRoom> findByCourseId(Long courseId);
}
