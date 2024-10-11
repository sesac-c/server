package sesac.server.group.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateRestaurantRequest(
        @Size(min = 1, max = 20, message = "INVALID_NAME_SIZE")
        String name,

        @Size(min = 1, max = 20, message = "INVALID_CATEGORY_SIZE")
        String category,

        @Size(min = 1, max = 150, message = "INVALID_ADDRESS_SIZE")
        String address,

        String latitude,

        String longitude
) {

    public boolean isAddressInfoChanged() {
        return address != null || latitude != null || longitude != null;
    }

    public boolean isAddressInfoComplete() {
        return address != null && latitude != null && longitude != null;
    }

}
