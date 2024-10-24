package sesac.server.chat.redis.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "course_chat_room", timeToLive = 86400)
@Data
@Builder
public class CourseChatRoomCache {

    @Id
    private String id;  // course_chat_room:courseId

    private Long chatRoomId;
    private Long courseId;
    private String name;
    private LocalDateTime lastMessageAt;
    private boolean active;
}
