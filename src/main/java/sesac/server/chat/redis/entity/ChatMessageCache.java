package sesac.server.chat.redis.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "course_chat_messages", timeToLive = 86400)
@Data
@Builder
public class ChatMessageCache {

    @Id
    private String id;  // chatRoomId:messageId

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private boolean delivered;
}