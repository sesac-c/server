package sesac.server.chat.dto.response;

import java.time.format.DateTimeFormatter;
import sesac.server.chat.entity.CourseChatMessage;

public record ChatMessageResponse(
        Long id,
        Long chatRoomId,
        Long senderId,
        String senderName,
        String content,
        String createdAt,
        boolean delivered
) {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

    public static ChatMessageResponse from(CourseChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getCourseChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getContent(),
                message.getCreatedAt().format(FORMATTER),
                message.isDelivered()
        );
    }
}
