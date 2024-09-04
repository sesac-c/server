package sesac.server.feed.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByPostId(Long postId);

    List<Reply> findByNoticeId(Long noticeId);
}
