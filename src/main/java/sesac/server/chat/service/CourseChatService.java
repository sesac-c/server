package sesac.server.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sesac.server.chat.repository.CourseChatMessageRepository;
import sesac.server.chat.repository.CourseChatRoomRepository;
import sesac.server.chat.repository.CourseChatRoomUserStatusRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseChatService {

    private final CourseChatMessageRepository chatMessageRepository;
    private final CourseChatRoomRepository chatRoomRepository;
    private final CourseChatRoomUserStatusRepository statusRepository;

}
