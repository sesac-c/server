package sesac.server.feed.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.request.ReplyRequest;
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.feed.dto.response.ReplyResponse;
import sesac.server.feed.entity.ArticleType;
import sesac.server.feed.entity.FeedType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.exception.ReplyErrorCode;
import sesac.server.feed.service.LikesService;
import sesac.server.feed.service.PostService;
import sesac.server.feed.service.ReplyService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("posts/{feedType}")             // feedType - CAMPUS, ALL
public class PostController {

    private final PostService postService;
    private final LikesService likesService;
    private final ReplyService replyService;
    private final BindingResultHandler bindingResultHandler;

    // -----------------------------------------------------------게시글 CRUD
    @GetMapping
    public ResponseEntity<List<PostListResponse>> getPostList(
            Pageable pageable,
            @PathVariable FeedType feedType,
            @ModelAttribute PostListRequest request
    ) {
        List<PostListResponse> posts = postService.getPostList(pageable, request, feedType);

        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<Void> createPost(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody CreatePostRequest createPostRequest,
            @PathVariable FeedType feedType,
            BindingResult bindingResult
    ) {

        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                PostErrorCode.REQUIRED_TITLE,
                PostErrorCode.INVALID_TITLE_SIZE,
                PostErrorCode.REQUIRED_CONTENT,
                PostErrorCode.INVALID_CONTENT_SIZE
        ));
        postService.createPost(principal.id(), feedType, createPostRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        PostResponse response = postService.getPostDetail(postId);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("{postId}")
    public ResponseEntity<Void> updatePost(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest updatePostRequest,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                PostErrorCode.INVALID_TITLE_SIZE,
                PostErrorCode.INVALID_CONTENT_SIZE
        ));

        postService.updatePost(principal, postId, updatePostRequest);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId) {
        postService.deletePost(principal, postId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("popular")
    public ResponseEntity<Void> getPopularPostList(@PathVariable FeedType feedType) {
        return null;
    }

    @GetMapping("search")
    public ResponseEntity<Void> searchPostList(@PathVariable FeedType feedType) {
        return null;
    }

    // -----------------------------------------------------------좋아요
    @PostMapping("{postId}/like")
    public ResponseEntity<Void> likePost(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId, @PathVariable FeedType feedType) {
        likesService.likeFeed(principal, postId, ArticleType.POST);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{postId}/like")
    public ResponseEntity<Void> cancelPostLike(@AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId, @PathVariable FeedType feedType) {
        likesService.cancelLikeFeed(principal, postId, ArticleType.POST);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------댓글
    @GetMapping("{postId}/replies")
    public ResponseEntity<List<ReplyResponse>> getReplyList(@PathVariable Long postId,
            @PathVariable FeedType feedType) {
        List<ReplyResponse> response = replyService.getReplyList(postId, ArticleType.POST);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{postId}/replies")
    public ResponseEntity<Void> createReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @PathVariable FeedType feedType,
            @Valid @RequestBody ReplyRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                ReplyErrorCode.REQUIRED_CONTENT,
                ReplyErrorCode.INVALID_CONTENT_SIZE
        ));
        replyService.createReply(principal, postId, request, ArticleType.POST);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{postId}/replies/{replyId}")
    public ResponseEntity<Void> updateReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long replyId,
            @PathVariable FeedType feedType,
            @Valid @RequestBody ReplyRequest request,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                ReplyErrorCode.REQUIRED_CONTENT,
                ReplyErrorCode.INVALID_CONTENT_SIZE
        ));
        replyService.updateReply(principal, replyId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{postId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long replyId,
            @PathVariable FeedType feedType
    ) {
        replyService.deleteReply(principal, replyId);

        return ResponseEntity.noContent().build();
    }
}
