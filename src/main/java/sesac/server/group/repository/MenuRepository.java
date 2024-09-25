package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
