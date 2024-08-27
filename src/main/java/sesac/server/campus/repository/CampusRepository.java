package sesac.server.campus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.campus.entity.Campus;

public interface CampusRepository extends JpaRepository<Campus, Long> {
    
}
