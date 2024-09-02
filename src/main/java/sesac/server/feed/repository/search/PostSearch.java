package sesac.server.feed.repository.search;

import java.util.List;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.PostListResponse;

public interface PostSearch {

    List<PostListResponse> searchPost(Pageable pageable);

}
