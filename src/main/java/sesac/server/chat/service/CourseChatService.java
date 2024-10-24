package sesac.server.chat.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Course;
import sesac.server.chat.dto.response.ChatMessageResponse;
import sesac.server.chat.entity.CourseChatMessage;
import sesac.server.chat.entity.CourseChatRoom;
import sesac.server.chat.exception.ChatErrorCode;
import sesac.server.chat.redis.entity.ChatMessageCache;
import sesac.server.chat.redis.entity.CourseChatRoomCache;
import sesac.server.chat.redis.repository.ChatMessageCacheRepository;
import sesac.server.chat.redis.repository.CourseChatRoomCacheRepository;
import sesac.server.chat.repository.CourseChatMessageRepository;
import sesac.server.chat.repository.CourseChatRoomRepository;
import sesac.server.common.exception.BaseException;
import sesac.server.user.entity.Student;
import sesac.server.user.service.CommonUserService;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class CourseChatService {

    private static final String TOPIC_PREFIX = "/topic/chat.course.";

    private static final String MESSAGE_CACHE_ID_FORMAT = "%d:%d";
    private static final String CHATROOM_KEY_PREFIX = "course_chat_room";
    private static final String CHATROOM_CACHE_ID_FORMAT = "%s:%d";

    private final CourseChatMessageRepository chatMessageRepository;
    private final CourseChatRoomRepository chatRoomRepository;
    private final CourseChatRoomCacheRepository courseChatRoomCacheRepository;
    private final ChatMessageCacheRepository chatMessageCacheRepository;

    private final CommonUserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public void createChatRoom(Course course) {
        validateChatRoomExistence(course);

        CourseChatRoom chatRoom = buildChatRoom(course);

        CourseChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);// MySQL에 저장
        saveChatRoomToRedis(savedChatRoom);                             // Redis에 저장
    }

    public void sendMessage(Long courseId, String content, CustomPrincipal principal) {
        // TODO: 캐시된 채팅방을 활용하기
        Student sender = userService.getStudentOrThrowException(principal);
        CourseChatRoom chatRoom = getActiveChatRoom(courseId);

        validateSenderIsCourseMember(sender, courseId);

        CourseChatMessage message = createChatMessage(chatRoom, sender, content);
        chatMessageRepository.save(message);
        chatRoom.updateLastMessageAt(message.getCreatedAt());

        saveMessageCache(chatRoom, message, sender);
        sendMessageToTopic(courseId, message);
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

    // 채팅방을 조회하고 활성화 상태를 확인
    private CourseChatRoom getActiveChatRoom(Long courseId) {
        CourseChatRoom chatRoom = chatRoomRepository.findByCourseId(courseId)
                .orElseThrow(() -> new BaseException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isActive()) {
            throw new BaseException(ChatErrorCode.CHAT_ROOM_INACTIVE);
        }
        return chatRoom;
    }

    // 발신자가 해당 강좌의 멤버인지 확인
    private void validateSenderIsCourseMember(Student sender, Long courseId) {
        if (!sender.getFirstCourse().getId().equals(courseId)) {
            throw new BaseException(ChatErrorCode.NOT_COURSE_MEMBER);
        }
    }

    // 메시지 생성
    private CourseChatMessage createChatMessage(CourseChatRoom chatRoom, Student sender,
            String content) {
        return CourseChatMessage.builder()
                .courseChatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .delivered(true)
                .build();
    }

    // 메시지 캐시 저장
    private void saveMessageCache(CourseChatRoom chatRoom, CourseChatMessage message,
            Student sender) {
        ChatMessageCache messageCache = ChatMessageCache.builder()
                .id(String.format(MESSAGE_CACHE_ID_FORMAT, chatRoom.getId(), message.getId()))
                .messageId(message.getId())
                .chatRoomId(chatRoom.getId())
                .senderId(sender.getId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .delivered(true)
                .build();
        chatMessageCacheRepository.save(messageCache);
    }

    // 메시지를 특정 경로로 전송
    private void sendMessageToTopic(Long courseId, CourseChatMessage message) {
        messagingTemplate.convertAndSend(
                TOPIC_PREFIX + courseId,
                ChatMessageResponse.from(message)
        );
    }
}