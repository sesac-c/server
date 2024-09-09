package sesac.server.user.dto.response;

import sesac.server.user.entity.Manager;

public record ManagerListResponse(
        Long id,
        String campusName,
        String address,
        String profileImage
) {

    public ManagerListResponse(Manager manager) {
        this(
                manager.getId(),
                manager.getCampus().getName(),
                manager.getCampus().getAddress(),
                manager.getProfileImage()
        );
    }
}
