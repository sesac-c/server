package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.Likes;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.user.entity.User;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserIdAndPostIdAndType(Long userId, Long postId, ArticleType type);

    boolean existsByUserIdAndNoticeIdAndType(Long userId, Long feedId, ArticleType articleType);

    void deleteByUserAndPostAndType(User user, Post feed, ArticleType articleType);

    void deleteByUserAndNoticeAndType(User user, Notice feed, ArticleType articleType);
}
