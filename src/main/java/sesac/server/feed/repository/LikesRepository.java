package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Likes;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserIdAndPostIdAndType(Long userId, Long postId, FeedType type);

    boolean existsByUserIdAndNoticeIdAndType(Long userId, Long feedId, FeedType feedType);
}
