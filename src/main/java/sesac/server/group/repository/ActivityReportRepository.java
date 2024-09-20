package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.ActivityReport;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {

}
