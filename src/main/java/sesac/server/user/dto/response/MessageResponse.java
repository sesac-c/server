package sesac.server.user.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String sender,
        String receiver,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {

}
