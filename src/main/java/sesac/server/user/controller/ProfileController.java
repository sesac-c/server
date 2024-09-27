package sesac.server.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Void> getProfile(@PathVariable Long userId) {
        return null;
    }

    @PostMapping("check-nickname")
    public ResponseEntity<Void> checkNickname() {
        return null;
    }

    @PutMapping("campus/{campusId}/course/{courseId}")
    public ResponseEntity<Void> updateCampus(@PathVariable Long campusId,
            @PathVariable Long courseId) {
        return null;
    }

}
