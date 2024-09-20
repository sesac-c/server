package sesac.server.group.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.group.entity.ActivityParticipant;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, Long> {

    @Query("select rmm.user.student.name from ActivityParticipant ap "
            + " join RunningMateMember rmm on ap.runningMateMember.id = rmm.id "
            + " where ap.activityReport.id = :activityReportId ")
    List<String> findParticipants(@Param("activityReportId") Long activityReportId);
}
