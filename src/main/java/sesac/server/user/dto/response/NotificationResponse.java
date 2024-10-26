package sesac.server.user.dto.response;

import sesac.server.user.entity.Notification;
import sesac.server.user.entity.NotificationType;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

public record NotificationResponse(
        Long userId,
        Long notificationId,
        String nickname,
        String profileImage,
        NotificationType type,
        boolean isRead
) {

    private NotificationResponse(Notification notification) {
        this(
                notification.getSender().getId(),
                notification.getId(),
                getUserName(notification.getSender()),
                getUserProfile(notification.getSender()),
                notification.getType(),
                notification.getIsRead()
        );
    }


    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(notification);
    }

    private static String getUserName(User user) {
        return user.getRole().equals(UserRole.MANAGER) ?
                user.getManager().getCampus().getName() :
                user.getStudent().getNickname();
    }

    private static String getUserProfile(User user) {
        return switch (user.getRole()) {
            case MANAGER -> user.getManager().getProfile();
            case STUDENT -> user.getStudent().getProfile();
            default -> null;
        };
    }
}
