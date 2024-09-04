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
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.service.LikesService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/{feedType}")
public class FeedNoticeController {

    private final LikesService likesService;
    private final BindingResultHandler bindingResultHandler;

//    @GetMapping("posts")
//    public ResponseEntity<List<PostListResponse>> getPostList(
//            Pageable pageable,
//            @PathVariable String feedType,
//            @ModelAttribute PostListRequest request
//    ) {
//        List<PostListResponse> posts = postService.getPostList(pageable, request, feedType);
//
//        return ResponseEntity.ok(posts);
//    }
//
//    @GetMapping("posts/{postId}")
//    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
//        PostResponse response = postService.getPostDetail(postId);
//
//        return ResponseEntity.ok().body(response);
//    }


    @PostMapping("notices/{noticeId}/like")
    public ResponseEntity<Void> likePost(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId) {
        likesService.likeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("notices/{noticeId}/like")
    public ResponseEntity<Void> cancelPostLike(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long noticeId) {
        likesService.cancelLikeFeed(principal, noticeId, ArticleType.NOTICE);

        return ResponseEntity.noContent().build();
    }
}