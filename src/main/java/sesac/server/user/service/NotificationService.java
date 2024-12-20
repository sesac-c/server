package sesac.server.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.feed.entity.Post;
import sesac.server.feed.repository.PostRepository;
import sesac.server.user.dto.response.NotificationResponse;
import sesac.server.user.entity.Notification;
import sesac.server.user.entity.NotificationType;
import sesac.server.user.entity.User;
import sesac.server.user.repository.NotificationRepository;
import sesac.server.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void postNotification(Long postId, Long senderId, NotificationType type) {
        Post post = postRepository.findById(postId).get();
        User user = post.getUser();
        User sender = userRepository.findById(senderId).get();

        Notification notification = Notification.builder()
                .post(post)
                .user(user)
                .sender(sender)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    public void followNotification(Long userId, Long senderId) {
        User user = userRepository.findById(userId).get();
        User sender = userRepository.findById(senderId).get();

        Notification notification = Notification.builder()
                .user(user)
                .sender(sender)
                .type(NotificationType.FOLLOW)
                .isRead(false)
                .build();

        notificationRepository.save(notification);

    }

    public List<NotificationResponse> getNotifications(Long userId, Pageable pageable) {
        List<Notification> list = notificationRepository.findAllByUserId(userId, pageable);

        return list.stream().map(NotificationResponse::from).toList();
    }

    public NotificationResponse getNotificationsDetail(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();

        notification.read();
        notificationRepository.save(notification);

        return NotificationResponse.from(notification);
    }

}
