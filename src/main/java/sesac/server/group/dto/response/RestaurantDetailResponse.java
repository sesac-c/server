package sesac.server.group.dto.response;

import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public record RestaurantDetailResponse(
        Long id,
        String name,
        String address,
        String longitude,
        String latitude,
        String category,
        GroupType type
) {

    private RestaurantDetailResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
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
