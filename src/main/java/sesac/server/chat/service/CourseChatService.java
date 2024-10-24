package sesac.server.chat.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesac.server.campus.entity.Course;
import sesac.server.chat.entity.CourseChatRoom;
import sesac.server.chat.exception.ChatErrorCode;
import sesac.server.chat.redis.entity.CourseChatRoomCache;
import sesac.server.chat.redis.repository.CourseChatRoomCacheRepository;
import sesac.server.chat.repository.CourseChatRoomRepository;
import sesac.server.common.exception.BaseException;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CourseChatService {

    private static final String CHATROOM_KEY_PREFIX = "course_chat_room";
    private static final String CHATROOM_CACHE_ID_FORMAT = "%s:%d";

    private final CourseChatRoomRepository chatRoomRepository;
    private final CourseChatRoomCacheRepository courseChatRoomCacheRepository;

    public void createChatRoom(Course course) {
        validateChatRoomExistence(course);

        CourseChatRoom chatRoom = buildChatRoom(course);

        CourseChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);// MySQL에 저장
        saveChatRoomToRedis(savedChatRoom);                             // Redis에 저장
    }


    // 새로운 채팅방이 존재하는지 확인
    private void validateChatRoomExistence(Course course) {
        if (chatRoomRepository.findByCourseId(course.getId()).isPresent()) {
            throw new BaseException(ChatErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }
    }

    // 채팅방 생성
    private CourseChatRoom buildChatRoom(Course course) {
        return CourseChatRoom.builder()
                .course(course)
                .name(course.getName())
                .lastMessageAt(LocalDateTime.now())
                .participantCount(0)
                .active(true)
                .build();
    }

    private void saveChatRoomToRedis(CourseChatRoom chatRoom) {
        Long courseId = chatRoom.getCourse().getId();
        String redisKey = String.format(CHATROOM_CACHE_ID_FORMAT, CHATROOM_KEY_PREFIX,
                courseId);

        CourseChatRoomCache chatRoomCache = CourseChatRoomCache.builder()
                .id(redisKey)
                .chatRoomId(chatRoom.getId())
                .courseId(courseId)
                .name(chatRoom.getName())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .active(chatRoom.isActive())
                .build();
        courseChatRoomCacheRepository.save(chatRoomCache);
    }
}
