package sesac.server.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesac.server.feed.entity.Post;
import sesac.server.feed.repository.PostRepository;
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

}
