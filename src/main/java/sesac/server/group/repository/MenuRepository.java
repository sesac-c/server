package sesac.server.group.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.Menu;
import sesac.server.group.entity.Restaurant;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByRestaurant(Restaurant restaurant);

}
