package sesac.server.user.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.user.dto.response.MessageResponse;
import sesac.server.user.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.receiver.id = :receiverId order by m.isRead asc , m.id desc")
    List<MessageResponse> findByReceiverId(@Param("receiverId") Long receiverId, Pageable pageable);

    @Query("select m from Message m where m.sender.id = :senderId order by m.isRead asc , m.id desc")
    List<MessageResponse> findBySenderId(@Param("senderId") Long senderId, Pageable pageable);
}
