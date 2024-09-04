package sesac.server.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Post;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.PostRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class ManagerPostService {

    private final PostRepository postRepository;

    public Page<PostListResponse> getPostList(Pageable pageable, PostListRequest request,
            FeedType type) {
        return postRepository.searchPostPage(pageable, request, type);
    }

    public PostResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        return new PostResponse(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        postRepository.delete(post);
    }
}
