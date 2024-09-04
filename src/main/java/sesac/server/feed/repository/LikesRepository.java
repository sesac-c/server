package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Likes;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.user.entity.User;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserIdAndPostIdAndType(Long userId, Long postId, FeedType type);

    boolean existsByUserIdAndNoticeIdAndType(Long userId, Long feedId, FeedType feedType);

    void deleteByUserAndPostAndType(User user, Post feed, FeedType feedType);

    void deleteByUserAndNoticeAndType(User user, Notice feed, FeedType feedType);
}
