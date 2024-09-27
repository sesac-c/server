package sesac.server.user.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.service.ProfileService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping("profiles")
    public ResponseEntity<Void> updateProfile() {
        return null;
    }

    @GetMapping("{userId}/profiles")
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        ProfileResponse response = profileService.getProfile(principal, userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(
            @Valid @RequestBody NicknameCheckRequest request,
            BindingResult bindingResult
    ) {

        BindingResultHandler.handle(bindingResult, List.of(
                UserErrorCode.REQUIRED_NICKNAME
        ));

        boolean isExistNickname = profileService.isExistNickname(request);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", isExistNickname);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("campus/{campusId}/course/{courseId}")
    public ResponseEntity<Void> updateCampus(@PathVariable Long campusId,
            @PathVariable Long courseId) {
        return null;
    }
}
