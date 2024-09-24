package sesac.server.group.dto.response;

import sesac.server.group.entity.Restaurant;

public record RestaurantListResponse(
        Long id,
        String name,
        String category,
        String address
) {

    private RestaurantListResponse(Restaurant restaurant) {
        this(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                restaurant.getAddress()
        );
    }

    public static RestaurantListResponse from(Restaurant restaurant) {
        return new RestaurantListResponse(restaurant);
    }
}
