package sesac.server.feed.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.feed.entity.Post;
import sesac.server.feed.repository.search.PostSearch;

public interface PostRepository extends JpaRepository<Post, Long>, PostSearch {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT DISTINCT p FROM Post p JOIN Likes l ON p.id = l.post.id WHERE l.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findPostsLikedByUserOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Post p JOIN Reply r ON p.id = r.post.id WHERE r.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findDistinctPostsByUserRepliesOrderByCreatedAtDesc(@Param("userId") Long userId);
}
