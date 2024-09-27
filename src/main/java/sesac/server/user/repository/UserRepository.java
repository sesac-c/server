package sesac.server.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.user.entity.User;
import sesac.server.user.repository.search.ProfileSearch;

public interface UserRepository extends JpaRepository<User, Long>, ProfileSearch {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
