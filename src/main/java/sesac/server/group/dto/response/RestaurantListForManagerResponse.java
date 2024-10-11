package sesac.server.group.dto.response;

import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public record RestaurantListForManagerResponse(
        Long id,
        String type,
        String name,
        String category,
        String address
) {

    private RestaurantListForManagerResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getType().equals(GroupType.CAMPUS) ? "캠퍼스" : "러닝메이트",
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getAddress()
        );
    }

    public static RestaurantListForManagerResponse from(Restaurant restaurant) {
        return new RestaurantListForManagerResponse(restaurant);
    }
}
