package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.repository.search.SearchRunningMate;

public interface RunningMateMemberRepository extends JpaRepository<RunningMateMember, Long>,
        SearchRunningMate {

}
