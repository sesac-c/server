package sesac.server.user.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.user.dto.request.MessageSendRequest;
import sesac.server.user.dto.response.MessageResponse;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.service.MessageService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("received")
    public ResponseEntity<List<MessageResponse>> getReceivedMessageList(
            @AuthPrincipal CustomPrincipal user,
            @PageableDefault Pageable pageable
    ) {
        List<MessageResponse> response = messageService.receivedMessage(user.id(), pageable);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("sent")
    public ResponseEntity<List<MessageResponse>> getSentMessageList(
            @AuthPrincipal CustomPrincipal user,
            @PageableDefault Pageable pageable
    ) {
        List<MessageResponse> response = messageService.sentMessage(user.id(), pageable);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{userId}")
    public ResponseEntity<Void> sendMessage(
            @AuthPrincipal CustomPrincipal sender,
            @PathVariable Long userId,
            @Valid @RequestBody MessageSendRequest request,
            BindingResult bindingResult
    ) {
        BindingResultHandler.handle(bindingResult, List.of(UserErrorCode.REQUIRED_MESSAGE));

        messageService.sendMessage(sender.id(), userId, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{messageId}")
    public ResponseEntity<MessageResponse> getMessage(
            @AuthPrincipal CustomPrincipal user,
            @PathVariable Long messageId
    ) {
        MessageResponse response = messageService.getMessage(user.id(), messageId);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthPrincipal CustomPrincipal receiver,
            @PathVariable Long messageId
    ) {
        messageService.deleteMessage(receiver.id(), messageId);

        return ResponseEntity.ok().build();
    }

}
