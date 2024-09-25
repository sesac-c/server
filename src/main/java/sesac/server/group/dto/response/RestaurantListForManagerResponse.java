package sesac.server.group.dto.response;

import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public record RestaurantListForManagerResponse(
        Long id,
        GroupType type,
        String name,
        String category,
        String address
) {

    private RestaurantListForManagerResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getType(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getAddress()
        );
    }

    public static RestaurantListForManagerResponse from(Restaurant restaurant) {
        return new RestaurantListForManagerResponse(restaurant);
    }
}
