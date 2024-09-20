package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
