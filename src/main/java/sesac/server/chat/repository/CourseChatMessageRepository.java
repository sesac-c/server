package sesac.server.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.chat.entity.CourseChatMessage;

public interface CourseChatMessageRepository extends JpaRepository<CourseChatMessage, Long> {

    Page<CourseChatMessage> findByCourseChatRoomIdOrderByCreatedAtDesc(Long chatRoomId,
            Pageable pageable);

}