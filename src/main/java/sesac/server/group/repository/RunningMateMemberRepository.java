package sesac.server.group.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.repository.search.SearchRunningMate;

public interface RunningMateMemberRepository extends JpaRepository<RunningMateMember, Long>,
        SearchRunningMate {

    @EntityGraph(attributePaths = {"user", "user.student"})
    Optional<RunningMateMember> findByIdAndRunningMateId(Long id, Long runningMateId);
}
