package sesac.server.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.Follow;
import sesac.server.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
