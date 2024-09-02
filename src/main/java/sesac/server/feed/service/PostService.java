package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.auth.exception.TokenErrorCode;
import sesac.server.auth.exception.TokenException;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.CreatePostRequest;
import sesac.server.feed.dto.PostListResponse;
import sesac.server.feed.dto.PostResponse;
import sesac.server.feed.dto.ReplyResponse;
import sesac.server.feed.dto.UpdatePostRequest;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostHashtag;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.HashtagRepository;
import sesac.server.feed.repository.PostHashtagRepository;
import sesac.server.feed.repository.PostRepository;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;

    public void createPost(Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TokenException(TokenErrorCode.UNACCEPT));

        Post post = Post.builder()
                .type(PostType.CAMPUS)
                .title(request.title())
                .content(request.content())
                .user(user)
                .build();

        postRepository.save(post);

        List<Hashtag> hashtags = hashtagRepository.findByNameIn(request.hashtag());
        List<Hashtag> newHashtags = request.hashtag()
                .stream()
                .filter(hashtag -> !hashtags.stream()
                        .map(r -> r.getName())
                        .toList()
                        .contains(hashtag))
                .map(hashtag -> Hashtag.builder()
                        .name(hashtag)
                        .build())
                .toList();

        hashtags.addAll(newHashtags);

        hashtagRepository.saveAll(hashtags);

        List<PostHashtag> postHashtags = hashtags.stream()
                .map(hashtag -> PostHashtag.builder()
                        .post(post)
                        .hashtag(hashtag)
                        .type(FeedType.POST)
                        .build())
                .toList();

        postHashtagRepository.saveAll(postHashtags);

    }

    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        List<ReplyResponse> replies = post.getReplies().stream()
                .map(ReplyResponse::new)
                .toList();

        return new PostResponse(post, replies);
    }

    public List<PostListResponse> getPosts(Pageable pageable) {
        List<PostListResponse> posts = postRepository.searchPost(pageable);

        return posts;
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

    private boolean hasPermission(CustomPrincipal principal, Long userId) {
        return principal.role().equals(UserRole.MANAGER) || userId == principal.id();
    }
}