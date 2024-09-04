package sesac.server.manager.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.feed.dto.request.CreateNoticeRequest;
import sesac.server.feed.dto.request.NoticeListRequest;
import sesac.server.feed.dto.request.UpdateNoticeRequest;
import sesac.server.feed.dto.response.NoticeListResponse;
import sesac.server.feed.dto.response.NoticeResponse;
import sesac.server.manager.service.ManagerNoticeService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("manager/notices")
public class ManagerNoticeController {

    private final ManagerNoticeService noticeService;
    private final BindingResultHandler bindingResultHandler;

    @PostMapping
    public ResponseEntity<Void> createNotice(
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

    @GetMapping
    public ResponseEntity<Page<NoticeListResponse>> getNoticeList(
            Pageable pageable,
            @ModelAttribute NoticeListRequest request
    ) {
        Page<NoticeListResponse> response = noticeService.getNoticeList(pageable, request,
                request.type());

        return ResponseEntity.ok(response);
    }

    @GetMapping("{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long noticeId) {
        NoticeResponse response = noticeService.getNotice(noticeId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("{noticeId}")
    public ResponseEntity<Void> updateNotice(
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

    @DeleteMapping("{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);

        return ResponseEntity.noContent().build();
    }
}
