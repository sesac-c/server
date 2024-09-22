package sesac.server.group.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.group.dto.response.ActivityReportListResponse;
import sesac.server.group.entity.ActivityReport;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {

    @Query("select ar from ActivityReport ar where ar.runningMate.id = :runningMateId order by ar.id desc limit 10")
    List<ActivityReportListResponse> findList(@Param("runningMateId") Long runningMateId,
            Pageable pageable);
}
