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
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.feed.dto.response.ReplyResponse;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Likes;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostHashtag;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.HashtagRepository;
import sesac.server.feed.repository.LikesRepository;
import sesac.server.feed.repository.NoticeRepository;
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
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final LikesRepository likesRepository;

    public Post createPost(Long userId, CreatePostRequest request) {
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

        return post;
    }

    public PostResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));

        List<ReplyResponse> replies = post.getReplies().stream()
                .map(ReplyResponse::new)
                .toList();

        return new PostResponse(post, replies);
    }

    public List<PostListResponse> getPostList(
            Pageable pageable,
            PostListRequest request,
            PostType type
    ) {
        List<PostListResponse> posts = postRepository.searchPost(pageable, request, type);

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

    @Transactional
    public void likeFeed(CustomPrincipal principal, Long feedId, FeedType feedType) {
        boolean alreadyLiked = isLiked(principal.id(), feedId, feedType);
        if (alreadyLiked) {
            throw new BaseException(PostErrorCode.ALREADY_LIKED);
        }

        Object feed = getFeedById(feedId, feedType);
        User user = userRepository.getReferenceById(principal.id());

        Likes like = getLikes(feed, user, feedType);

        likesRepository.save(like);
    }

    @Transactional
    public void cancelLikeFeed(CustomPrincipal principal, Long feedId, FeedType feedType) {
        boolean isLiked = isLiked(principal.id(), feedId, feedType);
        if (!isLiked) {
            throw new BaseException(PostErrorCode.NOT_LIKED);
        }

        Object feed = getFeedById(feedId, feedType);
        User user = userRepository.getReferenceById(principal.id());

        deleteLike(feed, user, feedType);
    }

    private boolean hasPermission(CustomPrincipal principal, Long userId) {
        return principal.role().equals(UserRole.MANAGER.toString()) ||
                principal.id().equals(userId);
    }

    private boolean isLiked(Long userId, Long feedId, FeedType feedType) {
        return switch (feedType) {
            case POST -> likesRepository.existsByUserIdAndPostIdAndType(userId, feedId, feedType);
            case NOTICE ->
                    likesRepository.existsByUserIdAndNoticeIdAndType(userId, feedId, feedType);
            default -> throw new IllegalArgumentException("없는 피드 타입입니다.");
        };
    }

    private Object getFeedById(Long feedId, FeedType feedType) {
        switch (feedType) {
            case POST:
                return postRepository.findById(feedId)
                        .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));
            case NOTICE:
                return noticeRepository.findById(feedId)
                        .orElseThrow(() -> new BaseException(PostErrorCode.NO_NOTICE));
            default:
                throw new IllegalArgumentException("없는 피드 타입입니다.");
        }
    }

    private Likes getLikes(Object feed, User user, FeedType feedType) {
        Likes.LikesBuilder builder = Likes.builder()
                .user(user)
                .type(feedType);

        if (feed instanceof Post) {
            return builder.post((Post) feed).build();
        } else if (feed instanceof Notice) {
            return builder.notice((Notice) feed).build();
        } else {
            throw new IllegalArgumentException("없는 피드 타입입니다.");
        }
    }

    private void deleteLike(Object feed, User user, FeedType feedType) {
        if (feed instanceof Post) {
            likesRepository.deleteByUserAndPostAndType(user, (Post) feed, feedType);
        } else if (feed instanceof Notice) {
            likesRepository.deleteByUserAndNoticeAndType(user, (Notice) feed, feedType);
        } else {
            throw new IllegalArgumentException("없는 피드 타입입니다.");
        }
    }
}