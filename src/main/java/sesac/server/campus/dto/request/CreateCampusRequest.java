package sesac.server.campus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCampusRequest(
        @NotBlank(message = "REQUIRED_NAME")
        @Size(min = 1, max = 20, message = "INVALID_NAME_SIZE")
        String name,

        @NotBlank(message = "REQUIRED_ADDRESS")
        @Size(min = 1, max = 150, message = "INVALID_ADDRESS_SIZE")
        String address
) {

}
