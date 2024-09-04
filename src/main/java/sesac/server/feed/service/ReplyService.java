package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.CreateReplyRequest;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.Reply;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.repository.NoticeRepository;
import sesac.server.feed.repository.PostRepository;
import sesac.server.feed.repository.ReplyRepository;
import sesac.server.user.entity.User;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final NoticeRepository noticeRepository;

    public void createReply(CustomPrincipal principal, Long articleId, CreateReplyRequest request,
            ArticleType articleType) {

        Object feed = getFeedById(articleId, articleType);
        User user = userRepository.getReferenceById(principal.id());

        Reply reply = getReply(feed, user, request.content(), articleType);

        replyRepository.save(reply);
    }

    private Object getFeedById(Long articleId, ArticleType articleType) {
        switch (articleType) {
            case POST:
                return postRepository.findById(articleId)
                        .orElseThrow(() -> new BaseException(PostErrorCode.NO_POST));
            case NOTICE:
                return noticeRepository.findById(articleId)
                        .orElseThrow(() -> new BaseException(PostErrorCode.NO_NOTICE));
            default:
                throw new IllegalArgumentException("없는 글 타입입니다.");
        }
    }

    private Reply getReply(Object feed, User user, String content, ArticleType articleType) {
        Reply.ReplyBuilder builder = Reply.builder()
                .user(user)
                .content(content)
                .type(articleType);

        if (feed instanceof Post) {
            return builder.post((Post) feed).build();
        } else if (feed instanceof Notice) {
            return builder.notice((Notice) feed).build();
        } else {
            throw new IllegalArgumentException("없는 글 타입입니다.");
        }
    }
}
