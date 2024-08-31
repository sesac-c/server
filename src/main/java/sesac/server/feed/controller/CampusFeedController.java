package sesac.server.feed.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import sesac.server.feed.dto.CreatePostRequest;
import sesac.server.feed.dto.PostResponse;
import sesac.server.feed.dto.UpdatePostRequest;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.service.PostService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/campus")
public class CampusFeedController {

    private final PostService postService;
    private final BindingResultHandler bindingResultHandler;

    @PostMapping("posts")
    public ResponseEntity<Void> createPost(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody CreatePostRequest createPostRequest,
            BindingResult bindingResult
    ) {

        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                PostErrorCode.REQUIRED_TITLE,
                PostErrorCode.INVALID_TITLE_SIZE,
                PostErrorCode.REQUIRED_CONTENT,
                PostErrorCode.INVALID_CONTENT_SIZE
        ));
        postService.createPost(principal.id(), createPostRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping("posts")
    public ResponseEntity<Page<PostResponse>> posts(Pageable pageable) {
        Page<PostResponse> posts = postService.getPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("posts/{postId}")
    public ResponseEntity<PostResponse> post(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);

        return ResponseEntity.ok().body(response);
    }


    @PutMapping("posts/{postId}")
    public void updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest updatePostRequest,
            BindingResult bindingResult
    ) {
        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                PostErrorCode.INVALID_TITLE_SIZE,
                PostErrorCode.INVALID_CONTENT_SIZE
        ));

        postService.updatePost(postId, updatePostRequest);
    }

    @DeleteMapping("posts/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }
}