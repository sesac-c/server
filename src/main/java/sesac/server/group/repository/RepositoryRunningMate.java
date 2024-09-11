package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.repository.search.SearchRunningMate;

public interface RepositoryRunningMate extends JpaRepository<RunningMate, Long>, SearchRunningMate {

}
