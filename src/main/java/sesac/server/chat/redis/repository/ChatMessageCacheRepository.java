package sesac.server.chat.redis.repository;

import org.springframework.data.repository.CrudRepository;
import sesac.server.chat.redis.entity.ChatMessageCache;

public interface ChatMessageCacheRepository extends CrudRepository<ChatMessageCache, String> {

}