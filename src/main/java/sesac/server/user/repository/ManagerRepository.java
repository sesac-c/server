package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

}
