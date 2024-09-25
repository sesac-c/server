package sesac.server.group.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m JOIN m.restaurant r WHERE r.id = :restaurantId AND r.type = :type")
    List<Menu> findByRestaurantIdAndType(@Param("restaurantId") Long restaurantId,
            @Param("type") GroupType type);


    @Query("SELECT m FROM Menu m JOIN FETCH m.restaurant r " +
            "WHERE m.id = :menuId AND r.id = :restaurantId AND r.type = :type")
    Optional<Menu> findByIdAndRestaurantIdAndType(
            @Param("menuId") Long menuId,
            @Param("restaurantId") Long restaurantId,
            @Param("type") GroupType type
    );
}
