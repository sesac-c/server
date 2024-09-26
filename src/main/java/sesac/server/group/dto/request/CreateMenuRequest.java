package sesac.server.group.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import sesac.server.group.entity.Menu;
import sesac.server.group.entity.Restaurant;

public record CreateMenuRequest(
        @NotNull(message = "REQUIRED_NAME")
        @Size(min = 1, max = 20, message = "INVALID_NAME_SIZE")
        String name,

        @NotNull(message = "REQUIRED_PRICE")
        BigDecimal price
) {

    public Menu toEntity(Restaurant restaurant) {
        return Menu.builder()
                .name(name)
                .price(price)
                .restaurant(restaurant)
                .build();
    }

}
