package sesac.server.group.dto.request;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateMenuRequest(
        @Size(min = 1, max = 20, message = "INVALID_NAME_SIZE")
        String name,

        BigDecimal price
) {

}
