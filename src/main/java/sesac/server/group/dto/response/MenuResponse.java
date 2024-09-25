package sesac.server.group.dto.response;

import java.math.BigDecimal;
import sesac.server.group.entity.Menu;

public record MenuResponse(
        Long id,
        String name,
        BigDecimal price
) {

    private MenuResponse(Menu menu) {
        this(
                menu.getId(),
                menu.getName(),
                menu.getPrice()
        );
    }

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(menu);
    }
}
