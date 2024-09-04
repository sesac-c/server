package sesac.server.feed.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.service.PostService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/all-campus")
public class AllCampusFeedController {

    private final PostService postService;

    @PostMapping("posts/{postId}/like")
    public ResponseEntity<Void> likePost(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId) {
        postService.likeFeed(principal, postId, ArticleType.POST);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("posts/{postId}/like")
    public ResponseEntity<Void> cancelPostLike(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId) {
        postService.cancelLikeFeed(principal, postId, ArticleType.POST);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("notices/{noticeId}/like")
    public ResponseEntity<Void> likeNotice(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId) {
        postService.likeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("notices/{noticeId}/like")
    public ResponseEntity<Void> cancelNoticeLike(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId) {
        postService.cancelLikeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }
}