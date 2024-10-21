package sesac.server.user.dto.response;

import io.jsonwebtoken.lang.Strings;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import sesac.server.common.constants.AppConstants;
import sesac.server.user.entity.Message;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

@Slf4j
public record MessageResponse(
        Long id,
        Long senderId,
        String content,
        String sender,
        String receiver,
        String senderProfile,
        String receiverProfile,
        boolean isRead,
        LocalDateTime createdAt
) {

    private MessageResponse(Message message) {
        this(
                message.getId(),
                message.getSender().getId(),
                message.getContent(),
                MessageResponse.getUserName(message.getSender()),
                MessageResponse.getUserName(message.getReceiver()),
                MessageResponse.getUserProfile(message.getSender()),
                MessageResponse.getUserProfile(message.getReceiver()),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }

    public static MessageResponse from(Message message) {
        return new MessageResponse(message);
    }

    private static String getUserName(User user) {
        return user.getRole().equals(UserRole.MANAGER) ?
                user.getManager().getCampus().getName() :
                user.getStudent().getNickname();
    }

    private static String getUserProfile(User user) {
        String profileImage = user.getRole().equals(UserRole.MANAGER) ?
                user.getManager().getProfileImage() :
                user.getStudent().getProfileImage();

        return Strings.hasText(profileImage) ? profileImage : AppConstants.DEFAULT_PROFILE_IMAGE;
    }
}
