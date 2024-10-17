package sesac.server.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.dto.response.UserArchiveResponse;
import sesac.server.user.service.UserService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    // -----------------------------------------------------------유저 목록
    @GetMapping("info")
    public ResponseEntity<Map<String, String>> getUserInfo(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(principal.id()));

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("account-info")
    public ResponseEntity<Map<String, String>> getUserAccountInfo(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, String> response = userService.getUserAccountInfo(principal);
        response.put("id", principal.id().toString());

        return ResponseEntity.ok().body(response);
    }


    @GetMapping("search-students")
    public ResponseEntity<List<StudentListResponse>> getSearchStudentList(
            @Param("nickname") String nickname
    ) {
        List<StudentListResponse> response = userService.getSearchStudentList(nickname);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("managers")
    public ResponseEntity<List<ManagerListResponse>> getManagerList() {
        List<ManagerListResponse> response = userService.getManagerList();

        return ResponseEntity.ok().body(response);
    }

    // -----------------------------------------------------------알림
    @GetMapping("notifications")
    public ResponseEntity<Void> getNotification() {
        return null;
    }

    // -----------------------------------------------------------작성 이력
    @GetMapping("{userId}/posts")
    public ResponseEntity<List<UserArchiveResponse>> getUserPosts(
            @PathVariable Long userId
    ) {
        List<UserArchiveResponse> response = userService.getUserPosts(userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("likes")
    public ResponseEntity<List<UserArchiveResponse>> getUserLikePosts(
            @AuthPrincipal CustomPrincipal principal
    ) {
        List<UserArchiveResponse> response = userService.getUserLikePosts(principal);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("replies")
    public ResponseEntity<List<UserArchiveResponse>> getUserReplyPosts(
            @AuthPrincipal CustomPrincipal principal) {
        List<UserArchiveResponse> response = userService.getUserReplyPosts(principal);
        return ResponseEntity.ok().body(response);
    }

    // -----------------------------------------------------------매니저 권한
    @GetMapping("students")
    public ResponseEntity<PageResponse<SearchStudentResponse>> getStudentList(
            @AuthPrincipal CustomPrincipal manager,
            @ModelAttribute SearchStudentRequest searchStudentRequest,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        PageResponse<SearchStudentResponse> response =
                userService.getStudentList(manager.id(), pageable, searchStudentRequest);

        return ResponseEntity.ok(response);

    }

    @GetMapping("students/{userId}")
    public ResponseEntity<StudentDetailResponse> getStudent(@PathVariable Long userId) {
        StudentDetailResponse response = userService.getStudent(userId);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("students/{userId}")
    public ResponseEntity<Void> updateStudent(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long userId,
            @RequestBody UpdateStudentRequest request) {

        userService.updateStudent(manager.id(), userId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("students/{userId}")
    public ResponseEntity<Void> acceptStudent(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long userId,
            @RequestBody AcceptStatusRequest request) {

        userService.acceptStudent(manager.id(), userId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/students/{userId}")
    public ResponseEntity<Void> deleteStudent(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long userId) {

        userService.deleteStudent(manager.id(), userId);

        return ResponseEntity.ok().build();
    }
}
