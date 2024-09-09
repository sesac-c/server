package sesac.server.campus.dto.response;

import java.time.LocalDateTime;
import sesac.server.campus.entity.Campus;

public record CampusDetailResponse(
        Long id,
        String name,
        String address,
        LocalDateTime createdAt
) {

    private CampusDetailResponse(Campus campus) {
        this(
                campus.getId(),
                campus.getName(),
                campus.getAddress(),
                campus.getCreatedAt()
        );
    }

    public static CampusDetailResponse from(Campus campus) {
        return new CampusDetailResponse(campus);
    }
}
