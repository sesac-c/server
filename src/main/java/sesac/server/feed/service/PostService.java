package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.account.exception.AccountException;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.feed.dto.response.ExtendedPostListResponse;
import sesac.server.feed.dto.response.PopularPostResponse;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.LikesRepository;
import sesac.server.feed.repository.PostRepository;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final HashtagService hashtagService;

    public Post createPost(Long userId, PostType postType, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException(UserErrorCode.NO_USER));

        Post post = request.toEntity(user, postType);

        postRepository.save(post);
        List<Hashtag> hashtags = hashtagService.saveHashTags(request.hashtag());
        hashtagService.savePostHashtags(hashtags, post);

        return post;
    }

    public PostResponse getPostDetail(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        boolean likesStatus = likesRepository.existsByUserIdAndPostId(userId, postId);

        return PostResponse.from(userId, post, likesStatus);
    }

    public PageResponse<PostListResponse> getPostList(
            Pageable pageable,
            PostListRequest request,
            PostType postType
    ) {
        Page<PostListResponse> posts = postRepository.searchPostPage(pageable, request, postType);

        return new PageResponse<>(posts);
    }

    public void updatePost(CustomPrincipal principal, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        if (!hasPermission(principal, post.getUser().getId())) {
            throw new BaseException(PostErrorCode.NO_PERMISSION);
        }

        post.update(request);
        postRepository.save(post);
    }

    public void deletePost(CustomPrincipal principal, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        if (!hasPermission(principal, post.getUser().getId())) {
            throw new BaseException(PostErrorCode.NO_PERMISSION);
        }

        postRepository.delete(post);
    }

    public List<PopularPostResponse> getPopularPostList() {
        return postRepository.popularPosts();
    }

    public List<Post> getUserPostList(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }


    public List<Post> getUserLikePostList(Long userId) {
        return postRepository.findPostsLikedByUserOrderByCreatedAtDesc(userId);
    }

    public List<Post> getUserReplyPostList(Long userId) {
        return postRepository.findDistinctPostsByUserRepliesOrderByCreatedAtDesc(userId);
    }

    private boolean hasPermission(CustomPrincipal principal, Long userId) {
        return principal.role().equals(UserRole.MANAGER.toString()) ||
                principal.id().equals(userId);
    }

    public PageResponse<ExtendedPostListResponse> getExtendedPostList(
            Pageable pageable,
            PostListRequest request,
            PostType postType
    ) {

        Page<ExtendedPostListResponse> response = postRepository.searchExtendedPostPage(pageable,
                request, postType);
        return new PageResponse(response);
    }
}