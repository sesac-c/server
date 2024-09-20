package sesac.server.group.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.repository.search.SearchRunningMate;

public interface RunningMateMemberRepository extends JpaRepository<RunningMateMember, Long>,
        SearchRunningMate {

    @EntityGraph(attributePaths = {"user", "user.student"})
    Optional<RunningMateMember> findByUserIdAndRunningMateId(Long userId, Long runningMateId);

    @Query("select rmm from RunningMateMember rmm where rmm.runningMate.id = :runningMateId and rmm.user.id in(:ids)")
    @EntityGraph(attributePaths = {"user", "user.student"})
    List<RunningMateMember> findByRunningMateIdAndUserIds(Long runningMateId, List<Long> ids);
}
