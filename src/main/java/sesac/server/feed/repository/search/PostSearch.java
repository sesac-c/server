package sesac.server.feed.repository.search;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.entity.FeedType;

public interface PostSearch {

    List<PostListResponse> searchPost(Pageable pageable, PostListRequest request, FeedType type);

    Page<PostListResponse> searchPostPage(Pageable pageable, PostListRequest request,
            FeedType type);
}
