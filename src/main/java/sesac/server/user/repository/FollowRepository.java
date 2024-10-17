package sesac.server.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.user.entity.Follow;
import sesac.server.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findAllByFollowingOrderByCreatedAtDesc(User following);

    List<Follow> findAllByFollowerOrderByCreatedAtDesc(User follower);

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :followerId")
    List<Long> findAllFollowingIdsByFollowerId(@Param("followerId") Long followerId);
}
