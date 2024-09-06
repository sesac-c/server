package sesac.server.feed.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.feed.dto.request.ReplyRequest;
import sesac.server.feed.dto.response.ReplyResponse;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.exception.ReplyErrorCode;
import sesac.server.feed.service.LikesService;
import sesac.server.feed.service.ReplyService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("notices/{feedType}")             // feedType - ALL, GROUP
public class NoticeController {

    private final LikesService likesService;
    private final ReplyService replyService;
    private final BindingResultHandler bindingResultHandler;

    // -----------------------------------------------------------공지 CRUD
    @GetMapping
    public ResponseEntity<Void> getNoticeList(@PathVariable String feedType) {
        return null;
    }

    @GetMapping("important")
    public ResponseEntity<Void> getImportantNotices(@PathVariable String feedType) {
        return null;
    }

    @GetMapping("{noticeId}")
    public ResponseEntity<Void> getNotice(
            @PathVariable String feedType, @PathVariable String noticeId
    ) {
        return null;
    }

    // 매니저 권한: 공지 생성
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<Void> createNotice(@PathVariable String feedType) {
        return null;
    }

    // 매니저 권한: 공지 수정
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("{noticeId}")
    public ResponseEntity<Void> updateNotice(
            @PathVariable String feedType, @PathVariable String noticeId
    ) {
        return null;
    }

    // 매니저 권한: 공지 삭제
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable String feedType, @PathVariable String noticeId
    ) {
        return null;
    }

    // -----------------------------------------------------------좋아요
    @PostMapping("{noticeId}/like")
    public ResponseEntity<Void> likePost(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable FeedType feedType
    ) {
        likesService.likeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{noticeId}/like")
    public ResponseEntity<Void> cancelPostLike(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable FeedType feedType
    ) {
        likesService.cancelLikeFeed(principal, noticeId, ArticleType.NOTICE);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------댓글
    @GetMapping("{noticeId}/replies")
    public ResponseEntity<List<ReplyResponse>> getReplyList(@PathVariable Long noticeId,
            @PathVariable FeedType feedType) {
        List<ReplyResponse> response = replyService.getReplyList(noticeId, ArticleType.NOTICE);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{noticeId}/replies")
    public ResponseEntity<Void> createReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable FeedType feedType,
            @Valid @RequestBody ReplyRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                ReplyErrorCode.REQUIRED_CONTENT,
                ReplyErrorCode.INVALID_CONTENT_SIZE
        ));
        replyService.createReply(principal, noticeId, request, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{noticeId}/replies/{replyId}")
    public ResponseEntity<Void> updateReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long replyId,
            @PathVariable FeedType feedType,
            @Valid @RequestBody ReplyRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                ReplyErrorCode.REQUIRED_CONTENT,
                ReplyErrorCode.INVALID_CONTENT_SIZE
        ));
        replyService.updateReply(principal, replyId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{noticeId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long replyId,
            @PathVariable FeedType feedType
    ) {
        replyService.deleteReply(principal, replyId);

        return ResponseEntity.noContent().build();
    }
}
