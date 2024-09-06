package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Likes;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.user.entity.User;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndNoticeId(Long userId, Long feedId);

    void deleteByUserAndPost(User user, Post feed);

    void deleteByUserAndNotice(User user, Notice feed);
}
