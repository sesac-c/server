package sesac.server.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import sesac.server.user.entity.Message;
import sesac.server.user.entity.User;

public record MessageSendRequest(
        @NotBlank(message = "REQUIRED_MESSAGE") String content
) {

    public Message toEntity(User sender, User receiver) {
        return Message.builder()
                .content(content)
                .sender(sender)
                .receiver(receiver)
                .isRead(false)
                .build();
    }
}
