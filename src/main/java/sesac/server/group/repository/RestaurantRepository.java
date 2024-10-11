package sesac.server.group.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.campus.entity.Campus;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCampusOrderByIdDesc(Campus campus);

    List<Restaurant> findByCampusAndNameContainingOrderByIdDesc(Campus campus, String name);

    List<Restaurant> findByCampusAndTypeOrderByIdDesc(Campus campus, GroupType type);

    Optional<Restaurant> findByIdAndType(Long restaurantId, GroupType groupType);
}
