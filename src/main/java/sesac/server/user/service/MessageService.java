package sesac.server.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.common.exception.BaseException;
import sesac.server.user.dto.request.MessageSendRequest;
import sesac.server.user.dto.response.MessageResponse;
import sesac.server.user.entity.Message;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.MessageRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final UserService userService;
    private final MessageRepository messageRepository;

    public void sendMessage(Long senderId, Long receiverId, MessageSendRequest request) {
        User sender = userService.getUserOrThrowException(senderId, UserErrorCode.NO_USER);

        User receiver = userService.getUserOrThrowException(receiverId, UserErrorCode.NO_RECEIVER);

        Message message = request.toEntity(sender, receiver);

        messageRepository.save(message);
    }

    public List<MessageResponse> receivedMessage(Long userId, Pageable pageable) {
        return messageRepository.findByReceiverId(userId, pageable);
    }

    public List<MessageResponse> sentMessage(Long userId, Pageable pageable) {
        return messageRepository.findBySenderId(userId, pageable);
    }

    public MessageResponse getMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MESSAGE));

        if (!message.getReceiver().getId().equals(userId) &&
                !message.getSender().getId().equals(userId)) {
            throw new BaseException(UserErrorCode.NO_MESSAGE);
        }

        return MessageResponse.from(message);
    }


    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MESSAGE));

        if (!message.getReceiver().getId().equals(userId)) {
            throw new BaseException(UserErrorCode.NO_MESSAGE);
        }

        messageRepository.delete(message);
    }

}
