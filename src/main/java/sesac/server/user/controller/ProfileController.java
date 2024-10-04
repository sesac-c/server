package sesac.server.user.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.user.dto.request.CourseChangeRequestRequest;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.request.UpdateProfileRequest;
import sesac.server.user.dto.response.CourseChangeRequestResponse;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.service.ProfileService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class ProfileController {

    private final ProfileService profileService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("student/profiles")
    public ResponseEntity<StudentProfileFormResponse> getStudentProfileForm(
            @AuthPrincipal CustomPrincipal customPrincipal
    ) {
        StudentProfileFormResponse response = profileService.getStudentProfileForm(customPrincipal);

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("student/profiles")
    public ResponseEntity<Void> updateStudentProfile(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request,
            BindingResult bindingResult
    ) {
        validateProfileInput(bindingResult);
        profileService.updateProfile(principal, request);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("manager/profiles")
    public ResponseEntity<Map<String, String>> getManagerProfileForm(
            @AuthPrincipal CustomPrincipal customPrincipal
    ) {
        String profileImage = profileService.getManagerProfileForm(customPrincipal);
        Map<String, String> response = new HashMap<>();
        response.put("profileImage", profileImage);

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("manager/profiles")
    public ResponseEntity<Void> updateManagerProfile(
            @AuthPrincipal CustomPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request,
            BindingResult bindingResult
    ) {
        validateProfileInput(bindingResult);
        profileService.updateProfile(principal, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("{userId}/profiles")
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        ProfileResponse response = profileService.getProfile(principal, userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(
            @Valid @RequestBody NicknameCheckRequest request,
            BindingResult bindingResult
    ) {

        BindingResultHandler.handle(bindingResult, List.of(
                UserErrorCode.REQUIRED_NICKNAME
        ));

        boolean isExistNickname = profileService.isExistNickname(request);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exist", isExistNickname);

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("campus/{campusId}/course/{courseId}")
    public ResponseEntity<Void> requestChangeCourse(
            @AuthPrincipal CustomPrincipal principal,
            @PathVariable Long campusId,
            @PathVariable Long courseId) {

        profileService.requestChangeCourse(principal, campusId, courseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("course-changes")
    public ResponseEntity<PageResponse<CourseChangeRequestResponse>> getCourseChangeRequestList(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String status
    ) {
        PageResponse<CourseChangeRequestResponse> response = profileService.getCourseChangeRequestList(
                pageable, status
        );
        return ResponseEntity.ok().body(response);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("course-changes/{courseChangeRequestId}")
    public ResponseEntity<Void> reviewCourseChangeRequest(
            @Valid @RequestBody CourseChangeRequestRequest request,
            BindingResult bindingResult,
            @PathVariable Long courseChangeRequestId
    ) {
        validateCourseChangeRequestInput(bindingResult);
        profileService.processCourseChangeRequest(courseChangeRequestId, request);
        return ResponseEntity.noContent().build();
    }

    private void validateProfileInput(BindingResult bindingResult) {
        BindingResultHandler.handle(bindingResult, List.of(
                UserErrorCode.INVALID_NICKNAME,
                UserErrorCode.INVALID_NICKNAME_SIZE,
                UserErrorCode.INVALID_NICKNAME_COMBINATION
        ));
    }

    private void validateCourseChangeRequestInput(BindingResult bindingResult) {
        BindingResultHandler.handle(bindingResult, List.of(
                UserErrorCode.NO_COURSE_CHANGE_REQUEST_STATUS
        ));
    }
}
