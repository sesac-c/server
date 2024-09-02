package sesac.server.feed.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.feed.entity.Hashtag;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {

    List<Hashtag> findByNameIn(List<String> names);
}
