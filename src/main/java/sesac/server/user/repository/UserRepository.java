package sesac.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
