package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.ActivityParticipant;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, Long> {

}
