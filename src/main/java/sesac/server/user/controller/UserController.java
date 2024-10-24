package sesac.server.user.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdatePasswordRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.NotificationResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.dto.response.UserArchiveResponse;
import sesac.server.user.service.NotificationService;
import sesac.server.user.service.UserService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final BindingResultHandler bindingResultHandler;

    // -----------------------------------------------------------유저 목록
    @GetMapping("id")
    public ResponseEntity<Map<String, Long>> getUserId(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, Long> response = new HashMap<>();
        response.put("id", principal.id());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("courseId")
    public ResponseEntity<Map<String, Long>> getUserCourseId(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, Long> response = userService.getCourseId(principal);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("info")
    public ResponseEntity<Map<String, String>> getUserInfo(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, String> response = userService.getUserInfo(principal);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("account-info")
    public ResponseEntity<Map<String, String>> getUserAccountInfo(
            @AuthPrincipal CustomPrincipal principal
    ) {
        Map<String, String> response = userService.getUserAccountInfo(principal);
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
    public ResponseEntity<List<NotificationResponse>> getNotification(
            @AuthPrincipal CustomPrincipal user,
            @PageableDefault Pageable pageable

    ) {
        Sort sort = Sort.by(Sort.Order.asc("isRead"), Sort.Order.desc("id"));
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                sort);
        List<NotificationResponse> response = notificationService.getNotifications(user.id(),
                sortedPageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("notifications/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationDetails(
            @AuthPrincipal CustomPrincipal user,
            @PathVariable Long notificationId

    ) {
        NotificationResponse response = notificationService.getNotificationsDetail(
                notificationId);
        return ResponseEntity.ok().body(response);
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

    // -----------------------------------------------------------비밀번호 업데이트
    @PatchMapping("update-password")
    public ResponseEntity<Void> updatePassword(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody UpdatePasswordRequest request,
            BindingResult bindingResult
    ) {

        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                AccountErrorCode.REQUIRED_UUID,
                AccountErrorCode.REQUIRED_PASSWORD,
                AccountErrorCode.INVALID_PASSWORD_PATTERN,
                AccountErrorCode.REQUIRED_PASSWORD_CONFIRM,
                AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM
        ));

        userService.updatePassword(principal, request);
        return ResponseEntity.ok().build();
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
