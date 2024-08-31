package sesac.server.feed.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.PostResponse;

public interface PostSearch {

    Page<PostResponse> searchPost(Pageable pageable);

}
