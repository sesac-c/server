package sesac.server.group.dto.response;

import java.math.BigDecimal;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public record RestaurantDetailResponse(
        Long id,
        String name,
        Long addressId,
        String address,
        BigDecimal longitude,
        BigDecimal latitude,
        String category,
        GroupType type
) {

    private RestaurantDetailResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddressId(),
                restaurant.getAddress(),
                restaurant.getLongitude(),
                restaurant.getLatitude(),
                restaurant.getCategory(),
                restaurant.getType()
        );
    }

    public static RestaurantDetailResponse from(Restaurant restaurant) {
        return new RestaurantDetailResponse(restaurant);
    }
}
