package sesac.server.feed.repository.search;

import java.util.List;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.PostResponse;

public interface PostSearch {

    List<PostResponse> searchPost(Pageable pageable);

}
