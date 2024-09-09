package sesac.server.campus.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCampusRequest(
        @Size(min = 1, max = 50, message = "INVALID_NAME_SIZE")
        String name,

        @Size(min = 1, max = 100, message = "INVALID_ADDRESS_SIZE")
        String address
) {

}
