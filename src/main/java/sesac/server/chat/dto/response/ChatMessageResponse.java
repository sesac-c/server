package sesac.server.chat.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.chat.entity.CourseChatMessage;

public record ChatMessageResponse(
        Long id,
        Long chatRoomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime createdAt,
        boolean isMine,
        boolean delivered
) {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

    public static ChatMessageResponse from(CourseChatMessage message, CustomPrincipal principal) {
        return new ChatMessageResponse(
                message.getId(),
                message.getCourseChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getContent(),
                message.getCreatedAt(),
//                message.getCreatedAt().format(FORMATTER),
                message.getSender().getId().equals(principal.id()),
                message.isDelivered()
        );
    }
}
