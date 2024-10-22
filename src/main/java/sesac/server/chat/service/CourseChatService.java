package sesac.server.chat.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesac.server.campus.entity.Course;
import sesac.server.chat.entity.CourseChatRoom;
import sesac.server.chat.exception.ChatErrorCode;
import sesac.server.chat.repository.CourseChatRoomRepository;
import sesac.server.common.exception.BaseException;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseChatService {

    private final CourseChatRoomRepository chatRoomRepository;

    public void createChatRoom(Course course) {
        validateChatRoomExistence(course);

        CourseChatRoom chatRoom = buildChatRoom(course);
        chatRoomRepository.save(chatRoom);
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
}
