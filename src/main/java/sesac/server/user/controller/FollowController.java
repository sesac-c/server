package sesac.server.user.controller;

import java.util.List;
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
import sesac.server.user.dto.response.FollowResponse;
import sesac.server.user.service.FollowService;
import sesac.server.user.service.NotificationService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class FollowController {

    private final FollowService followService;
    private final NotificationService notificationService;

    @GetMapping("{userId}/follows")
    public ResponseEntity<List<FollowResponse>> getUserFollows(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 사용자가 팔로우 하는 사람의 목록
        List<FollowResponse> response = followService.getFollowingList(principal, userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{userId}/follow")
    public ResponseEntity<Void> followUser(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 팔로우 하기
        followService.followUser(principal, userId);
        notificationService.followNotification(userId, principal.id());
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
    public ResponseEntity<List<FollowResponse>> getUserFollowers(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        // 사용자를 팔로우하는 사람들의 목록
        List<FollowResponse> response = followService.getFollowerList(principal, userId);
        return ResponseEntity.ok().body(response);
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
