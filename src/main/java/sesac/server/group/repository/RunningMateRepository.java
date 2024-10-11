package sesac.server.group.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.repository.search.SearchRunningMate;

public interface RunningMateRepository extends JpaRepository<RunningMate, Long>, SearchRunningMate {

    @Query("select rm from RunningMate rm join RunningMateMember rmm on rm.id = rmm.runningMate.id where rmm.user.id = :userId")
    Optional<RunningMate> findByUserId(@Param("userId") Long userId);
}
