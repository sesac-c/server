package sesac.server.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.group.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}
