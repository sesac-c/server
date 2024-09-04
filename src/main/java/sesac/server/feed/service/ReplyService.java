package sesac.server.feed.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.ReplyRequest;
import sesac.server.feed.dto.response.ReplyResponse;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.Reply;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.exception.ReplyErrorCode;
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

    public List<ReplyResponse> getReplyList(Long articleId, ArticleType articleType) {
        List<Reply> replies = switch (articleType) {
            case POST -> replyRepository.findByPostId(articleId);
            case NOTICE -> replyRepository.findByNoticeId(articleId);
            default -> throw new IllegalArgumentException("없는 글 타입입니다.");
        };
        return replies.stream()
                .map(ReplyResponse::new)
                .collect(Collectors.toList());
    }

    public void createReply(CustomPrincipal principal, Long articleId, ReplyRequest request,
            ArticleType articleType) {

        Object feed = getFeedById(articleId, articleType);
        User user = userRepository.getReferenceById(principal.id());

        Reply reply = getReply(feed, user, request.content(), articleType);

        replyRepository.save(reply);
    }

    public void updateReply(CustomPrincipal principal, Long replyId, ReplyRequest request) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new BaseException(ReplyErrorCode.NO_REPLY)
        );

        if (reply.getContent().equals(request.content())) {             // 이전 댓글내용과 같은지 확인
            throw new BaseException(ReplyErrorCode.CONTENT_SAME_AS_PREVIOUS);
        }
        if (!hasPermission(principal.id(), reply.getUser().getId())) {  // 댓글 수정 권한은 본인에게만
            throw new BaseException(ReplyErrorCode.NO_PERMISSION);
        }

        reply.updateReply(request.content());
    }

    public void deleteReply(CustomPrincipal principal, Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new BaseException(ReplyErrorCode.NO_REPLY)
        );

        if (!hasPermission(principal.id(), reply.getUser().getId())) {  // 댓글 삭제 권한은 본인에게만
            throw new BaseException(ReplyErrorCode.NO_PERMISSION);
        }

        replyRepository.delete(reply);
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

    private boolean hasPermission(Long userId, Long replyUserId) {
        return userId.equals(replyUserId);
    }
}
