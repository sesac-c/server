package sesac.server.chat.redis.repository;

import org.springframework.data.repository.CrudRepository;
import sesac.server.chat.redis.entity.CourseChatRoomCache;

public interface CourseChatRoomCacheRepository extends CrudRepository<CourseChatRoomCache, String> {

}