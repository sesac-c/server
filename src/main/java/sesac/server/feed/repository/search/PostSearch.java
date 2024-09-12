package sesac.server.feed.repository.search;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.ExtendedPostListResponse;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.entity.PostType;

public interface PostSearch {

    List<PostListResponse> searchPost(Pageable pageable, PostListRequest request, PostType type);

    Page<PostListResponse> searchPostPage(Pageable pageable, PostListRequest request,
            PostType type);

    Page<ExtendedPostListResponse> searchExtendedPostPage(Pageable pageable,
            PostListRequest request, PostType type);
}
