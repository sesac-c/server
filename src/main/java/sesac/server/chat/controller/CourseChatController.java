package sesac.server.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.chat.dto.request.ChatMessageRequest;
import sesac.server.chat.dto.response.ChatMessageResponse;
import sesac.server.chat.service.CourseChatService;
import sesac.server.common.dto.PageResponse;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CourseChatController {

    private final CourseChatService courseChatService;

    // 메세지 전송, 구독한 클라이언트에게 반환
    @MessageMapping("/course.{courseId}")
    public void sendMessage(
            @DestinationVariable Long courseId,
            @Payload @Valid ChatMessageRequest request,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        courseChatService.sendMessage(courseId, request.content(), principal);
    }

    // 읽음 처리

    // 채팅방 가져오기

    // 메시지 가져오기
    @GetMapping("chat/course/{courseId}/messages")
    public PageResponse<ChatMessageResponse> getMessages(
            @PathVariable Long courseId,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthPrincipal CustomPrincipal principal
    ) {
        return courseChatService.getMessages(principal, courseId, pageable);
    }
}
