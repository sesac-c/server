package sesac.server.user.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.common.exception.BaseException;
import sesac.server.user.dto.request.MessageSendRequest;
import sesac.server.user.dto.response.MessageResponse;
import sesac.server.user.entity.Message;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.MessageRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Service
public class MessageService extends CommonUserService {

    private final MessageRepository messageRepository;

    protected MessageService(UserRepository userRepository,
            StudentRepository studentRepository,
            ManagerRepository managerRepository,
            MessageRepository messageRepository
    ) {
        super(userRepository, studentRepository, managerRepository);
        this.messageRepository = messageRepository;
    }

    public void sendMessage(Long senderId, Long receiverId, MessageSendRequest request) {
        User sender = getUserOrThrowException(senderId, UserErrorCode.NO_USER);

        User receiver = getUserOrThrowException(receiverId, UserErrorCode.NO_RECEIVER);

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

        message.read();
        messageRepository.save(message);

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
