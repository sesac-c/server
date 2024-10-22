package sesac.server.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.chat.entity.CourseChatRoomUserStatus;

public interface CourseChatRoomUserStatusRepository extends
        JpaRepository<CourseChatRoomUserStatus, Long> {

}