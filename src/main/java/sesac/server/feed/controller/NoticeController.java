package sesac.server.feed.controller;

import static sesac.server.feed.exception.PostErrorCode.INVALID_CONTENT_SIZE;
import static sesac.server.feed.exception.PostErrorCode.INVALID_TITLE_SIZE;
import static sesac.server.feed.exception.PostErrorCode.REQUIRED_CONTENT;
import static sesac.server.feed.exception.PostErrorCode.REQUIRED_NOTICE_TYPE;
import static sesac.server.feed.exception.PostErrorCode.REQUIRED_TITLE;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.feed.dto.request.CreateNoticeRequest;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.request.ReplyRequest;
import sesac.server.feed.dto.request.UpdateNoticeRequest;
import sesac.server.feed.dto.response.ExtendedNoticeListResponse;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.dto.response.NoticeResponse;
import sesac.server.feed.dto.response.ReplyResponse;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.NoticeType;
import sesac.server.feed.exception.ReplyErrorCode;
import sesac.server.feed.service.LikesService;
import sesac.server.feed.service.NoticeService;
import sesac.server.feed.service.ReplyService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("notices/{noticeType}")             // noticeType - ALL, GROUP
public class NoticeController {

    private final NoticeService noticeService;
    private final LikesService likesService;
    private final ReplyService replyService;
    private final BindingResultHandler bindingResultHandler;

    // -----------------------------------------------------------공지 CRUD
    @GetMapping
    public ResponseEntity<Page<NoticeListResponse>> getNoticeList(
            @PathVariable NoticeType noticeType,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @ModelAttribute NoticeListRequest request
    ) {
        Page<NoticeListResponse> response = noticeService.getNoticeList(pageable, request,
                noticeType);

        return ResponseEntity.ok(response);
    }

    @GetMapping("important")
    public ResponseEntity<Void> getImportantNotices(@PathVariable NoticeType noticeType) {
        return null;
    }

    @GetMapping("{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(
            @PathVariable NoticeType noticeType, @PathVariable Long noticeId
    ) {
        NoticeResponse response = noticeService.getNotice(noticeId);

        return ResponseEntity.ok(response);
    }

    // 매니저 권한: 공지 생성
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<Void> createNotice(
            @PathVariable NoticeType noticeType,
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody CreateNoticeRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                REQUIRED_TITLE,
                INVALID_TITLE_SIZE,
                REQUIRED_CONTENT,
                INVALID_CONTENT_SIZE,
                REQUIRED_NOTICE_TYPE
        ));

        noticeService.createNotice(principal.id(), request);

        return ResponseEntity.ok().build();
    }

    // 매니저 권한: 공지 수정
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("{noticeId}")
    public ResponseEntity<Void> updateNotice(
            @PathVariable NoticeType noticeType,
            @PathVariable Long noticeId,
            @Valid @RequestBody UpdateNoticeRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                INVALID_TITLE_SIZE,
                INVALID_CONTENT_SIZE
        ));

        noticeService.updateNotice(noticeId, request);

        return ResponseEntity.noContent().build();
    }

    // 매니저 권한: 공지 삭제
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable NoticeType noticeType,
            @PathVariable Long noticeId
    ) {
        noticeService.deleteNotice(noticeId);

        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------좋아요
    @PostMapping("{noticeId}/like")
    public ResponseEntity<Void> likePost(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable NoticeType noticeType
    ) {
        likesService.likeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{noticeId}/like")
    public ResponseEntity<Void> cancelPostLike(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable NoticeType noticeType
    ) {
        likesService.cancelLikeFeed(principal, noticeId, ArticleType.NOTICE);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------댓글
    @GetMapping("{noticeId}/replies")
    public ResponseEntity<List<ReplyResponse>> getReplyList(@PathVariable Long noticeId,
            @PathVariable NoticeType noticeType) {
        List<ReplyResponse> response = replyService.getReplyList(noticeId, ArticleType.NOTICE);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{noticeId}/replies")
    public ResponseEntity<Void> createReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId,
            @PathVariable NoticeType noticeType,
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
            @PathVariable NoticeType noticeType,
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
            @PathVariable NoticeType noticeType
    ) {
        replyService.deleteReply(principal, replyId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manager")
    public ResponseEntity<PageResponse<ExtendedNoticeListResponse>> getExtendedNoticeList(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @PathVariable NoticeType noticeType,
            @ModelAttribute NoticeListRequest request
    ) {
        PageResponse<ExtendedNoticeListResponse> response =
                noticeService.getExtendedNoticeList(pageable, request, noticeType);

        return ResponseEntity.ok(response);
    }
}
