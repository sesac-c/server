package sesac.server.group.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import sesac.server.campus.entity.Campus;
import sesac.server.group.entity.GroupType;
import sesac.server.group.entity.Restaurant;

public record CreateRestaurantRequest(
        @NotNull(message = "REQUIRED_NAME")
        @Size(min = 1, max = 20, message = "INVALID_NAME_SIZE")
        String name,

        @NotNull(message = "REQUIRED_CATEGORY")
        @Size(min = 1, max = 20, message = "INVALID_CATEGORY_SIZE")
        String category,

        @NotNull(message = "REQUIRED_ADDRESS")
        @Size(min = 1, max = 150, message = "INVALID_ADDRESS_SIZE")
        String address,

        @NotNull(message = "REQUIRED_ADDRESS_ID")
        Long addressId,

        @NotNull(message = "REQUIRED_LATITUDE")
        BigDecimal latitude,

        @NotNull(message = "REQUIRED_LONGITUDE")
        BigDecimal longitude
) {

    public Restaurant toEntity(Campus campus, GroupType groupType) {
        return Restaurant.builder()
                .name(name)
                .addressId(addressId)
                .address(address)
                .longitude(longitude)
                .latitude(latitude)
                .category(category)
                .campus(campus)
                .type(groupType)
                .build();
    }

}
