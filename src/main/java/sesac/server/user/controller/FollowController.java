package sesac.server.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.user.service.FollowService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class FollowController {

    private final FollowService followService;

    @GetMapping("{userId}/follows")
    public ResponseEntity<Void> getUserFollows(@PathVariable Long userId) {
        return null;
    }

    @PostMapping("{userId}/follow")
    public ResponseEntity<Void> followUser(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 팔로우 하기
        followService.followUser(principal, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 팔로우 취소
        followService.unfollowUser(principal, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}/followers")
    public ResponseEntity<Void> getUserFollowers(@PathVariable Long userId) {
        return null;
    }

    @DeleteMapping("{userId}/follower")
    public ResponseEntity<Void> deleteUserFollower(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 팔로워 삭제
        followService.removeFollowing(principal, userId);
        return ResponseEntity.noContent().build();
    }
}
