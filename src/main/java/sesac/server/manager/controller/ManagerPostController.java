package sesac.server.manager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.manager.service.ManagerPostService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("manager/posts")
public class ManagerPostController {

    private final ManagerPostService managerPostService;

    @GetMapping
    public ResponseEntity<Page<PostListResponse>> postList(
            @ModelAttribute PostListRequest request,
            Pageable pageable
    ) {
        Page<PostListResponse> responses =
                managerPostService.getPostList(pageable, request, request.postType());

        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("{postId}")
    public ResponseEntity<PostResponse> postDetail(@PathVariable Long postId) {
        PostResponse response = managerPostService.getPostDetail(postId);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        managerPostService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
