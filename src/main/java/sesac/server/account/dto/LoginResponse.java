package sesac.server.account.dto;

import sesac.server.user.entity.UserRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String nickname,
        UserRole role
) {

}

