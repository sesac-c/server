package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
