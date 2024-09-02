package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Post;
import sesac.server.feed.repository.search.PostSearch;

public interface PostRepository extends JpaRepository<Post, Long>, PostSearch {

}
