package sesac.server.user.repository.search;

import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.user.dto.response.ProfileResponse;

public interface UserSearch {

    ProfileResponse getProfileResponse(Long profileUserId, CustomPrincipal principal);
}
