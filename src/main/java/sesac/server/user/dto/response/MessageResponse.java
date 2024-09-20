package sesac.server.user.dto.response;

import java.time.LocalDateTime;
import sesac.server.user.entity.Message;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

public record MessageResponse(
        Long id,
        String sender,
        String receiver,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {

    private MessageResponse(Message message, String sender, String receiver) {
        this(
                message.getId(),
                sender,
                receiver,
                message.getContent(),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }

    public static MessageResponse from(Message message) {
        User sender = message.getSender();
        User receiver = message.getReceiver();

        String senderName = sender.getRole().equals(UserRole.MANAGER) ?
                sender.getManager().getCampus().getName() :
                sender.getStudent().getName();

        String receiverName = receiver.getRole().equals(UserRole.MANAGER) ?
                receiver.getManager().getCampus().getName() :
                receiver.getStudent().getName();

        return new MessageResponse(message, senderName, receiverName);
    }
}
