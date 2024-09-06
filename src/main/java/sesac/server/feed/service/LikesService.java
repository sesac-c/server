package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.Likes;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.LikesRepository;
import sesac.server.feed.repository.NoticeRepository;
import sesac.server.feed.repository.PostRepository;
import sesac.server.user.entity.User;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final NoticeRepository noticeRepository;

    public void likeFeed(CustomPrincipal principal, Long articleId, ArticleType articleType) {
        boolean alreadyLiked = isLiked(principal.id(), articleId, articleType);
        if (alreadyLiked) {
            throw new BaseException(PostErrorCode.ALREADY_LIKED);
        }

        Object feed = getFeedById(articleId, articleType);
        User user = userRepository.getReferenceById(principal.id());

        Likes like = getLikes(feed, user, articleType);

        likesRepository.save(like);
    }

    public void cancelLikeFeed(CustomPrincipal principal, Long articleId, ArticleType articleType) {
        boolean isLiked = isLiked(principal.id(), articleId, articleType);
        if (!isLiked) {
            throw new BaseException(PostErrorCode.NOT_LIKED);
        }

        Object feed = getFeedById(articleId, articleType);
        User user = userRepository.getReferenceById(principal.id());

        deleteLike(feed, user, articleType);
    }

    private boolean isLiked(Long userId, Long articleId, ArticleType articleType) {
        return switch (articleType) {
            case POST -> likesRepository.existsByUserIdAndPostId(userId, articleId);
            case NOTICE -> likesRepository.existsByUserIdAndNoticeId(userId, articleId);
            default -> throw new IllegalArgumentException("없는 글 타입입니다.");
        };
    }


    private Object getFeedById(Long articleId, ArticleType articleType) {
        return switch (articleType) {
            case POST -> postRepository.findById(articleId)
                    .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));
            case NOTICE -> noticeRepository.findById(articleId)
                    .orElseThrow(() -> new BaseException(PostErrorCode.NO_NOTICE));
            default -> throw new IllegalArgumentException("없는 글 타입입니다.");
        };
    }

    private Likes getLikes(Object feed, User user, ArticleType articleType) {
        Likes.LikesBuilder builder = Likes.builder()
                .user(user);

        if (feed instanceof Post) {
            return builder.post((Post) feed).build();
        } else if (feed instanceof Notice) {
            return builder.notice((Notice) feed).build();
        } else {
            throw new IllegalArgumentException("없는 글 타입입니다.");
        }
    }

    private void deleteLike(Object feed, User user, ArticleType articleType) {
        if (feed instanceof Post) {
            likesRepository.deleteByUserAndPost(user, (Post) feed);
        } else if (feed instanceof Notice) {
            likesRepository.deleteByUserAndNotice(user, (Notice) feed);
        } else {
            throw new IllegalArgumentException("없는 글 타입입니다.");
        }
    }

}
