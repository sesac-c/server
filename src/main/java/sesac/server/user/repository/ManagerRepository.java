package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sesac.server.user.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT m.profileImage FROM Manager m WHERE m.id = :id")
    String findProfileImageById(Long id);

}
