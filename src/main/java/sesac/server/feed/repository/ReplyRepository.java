package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

}
