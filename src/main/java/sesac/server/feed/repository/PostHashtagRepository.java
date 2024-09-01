package sesac.server.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.PostHashtag;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Integer> {

}
