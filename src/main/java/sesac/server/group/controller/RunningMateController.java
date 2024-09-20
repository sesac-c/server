package sesac.server.group.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BindingResultHandler;
import sesac.server.group.dto.request.CreateActivityReportRequest;
import sesac.server.group.dto.request.CreateRunningMateMemberRequest;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateMemberRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.ActivityReportListResponse;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberListResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.group.service.RunningMateService;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("runningmates")
public class RunningMateController {

    private final RunningMateService runningMateService;

    @GetMapping("{runningmateId}/activities")
    public ResponseEntity<List<ActivityReportListResponse>> getActivityReportList(
            @PathVariable Long runningmateId,
            @PageableDefault Pageable pageable
    ) {
        List<ActivityReportListResponse> response =
                runningMateService.getActivityReportList(runningmateId, pageable);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{runningmateId}/activities")
    public ResponseEntity<Void> createActivityReport(
            @PathVariable Long runningmateId,
            @Valid @RequestBody CreateActivityReportRequest request,
            BindingResult bindingResult
    ) {
        BindingResultHandler.handle(bindingResult, List.of(
                RunningMateErrorCode.REQUIRED_DURATION,
                RunningMateErrorCode.REQUIRED_MAIN_CONTENT,
                RunningMateErrorCode.REQUIRED_ACHIEVEMENT_SUMMARY,
                RunningMateErrorCode.REQUIRED_PHOTO
        ));
        runningMateService.createActivityReport(runningmateId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{runningmateId}/activity-form")
    public ResponseEntity<Void> getActivityReportForm() {
        return null;
    }

    @GetMapping("{runningmateId}/activities/{activityId}")
    public ResponseEntity<Void> getActivityReport(@PathVariable Long activityId) {
        return null;
    }

    @PutMapping("{runningmateId}/trans-leader")
    public ResponseEntity<Void> transformLeader() {
        return null;
    }

    @DeleteMapping("{runningmateId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        return null;
    }

    // -----------------------------------------------------------매니저 권한
    // 러닝메이트 관리
    @GetMapping
    public ResponseEntity<PageResponse<SearchRunningMateResponse>> getRunningmateList(
            @ModelAttribute SearchRunningMateRequest request,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        PageResponse<SearchRunningMateResponse> response =
                runningMateService.getRunningmateList(pageable, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> createRunningmate(
            @Valid @RequestBody CreateRunningMateRequest request,
            BindingResult bindingResult
    ) {
        BindingResultHandler.handle(bindingResult, List.of(
                RunningMateErrorCode.REQUIRED_NAME,
                RunningMateErrorCode.REQUIRED_SUBJECT,
                RunningMateErrorCode.REQUIRED_GOAL,
                RunningMateErrorCode.REQUIRED_COURSE
        ));

        runningMateService.createRunningmate(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{runningmateId}")
    public ResponseEntity<RunningMateDetailResponse> getRunningmate(
            @PathVariable Long runningmateId) {
        RunningMateDetailResponse response = runningMateService.getRunningmate(runningmateId);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("{runningmateId}")
    public ResponseEntity<Void> updateRunningmate(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long runningmateId,
            @RequestBody UpdateRunningMateRequest request
    ) {
        runningMateService.updateRunningmate(manager.id(), runningmateId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{runningmateId}")
    public ResponseEntity<Void> deleteRunningmate(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long runningmateId
    ) {
        runningMateService.deleteRunningmate(manager.id(), runningmateId);
        return ResponseEntity.ok().build();
    }

    // 멤버 관리
    @GetMapping("{runningmateId}/members")
    public ResponseEntity<List<RunningMateMemberListResponse>> getRunningmateMemberList(
            @PathVariable Long runningmateId) {
        List<RunningMateMemberListResponse> response = runningMateService.getRunningmateMemberList(
                runningmateId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("{runningmateId}/members")
    public ResponseEntity<Void> createRunningmateMember(
            @PathVariable Long runningmateId,
            @Validated @RequestBody CreateRunningMateMemberRequest request,
            BindingResult bindingResult
    ) {
        BindingResultHandler.handle(bindingResult, List.of(
                RunningMateErrorCode.REQUIRED_USER,
                RunningMateErrorCode.REQUIRED_ROLE,
                RunningMateErrorCode.REQUIRED_PHONE,
                RunningMateErrorCode.INVALID_PHONE
        ));

        runningMateService.createRunningmateMember(runningmateId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<RunningMateMemberDetailResponse> getRunningmateMember(
            @PathVariable Long runningmateId, @PathVariable Long memberId
    ) {
        RunningMateMemberDetailResponse response =
                runningMateService.getRunningmateMember(runningmateId, memberId);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<Void> updateRunningmateMember(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long runningmateId,
            @PathVariable Long memberId,
            @Validated @RequestBody UpdateRunningMateMemberRequest request,
            BindingResult bindingResult
    ) {
        BindingResultHandler.handle(bindingResult, List.of(
                RunningMateErrorCode.INVALID_PHONE
        ));

        runningMateService.updateRunningmateMember(manager.id(), runningmateId, memberId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<Void> deleteRunningmateMember(
            @AuthPrincipal CustomPrincipal manager,
            @PathVariable Long runningmateId,
            @PathVariable Long memberId
    ) {

        runningMateService.deleteRunningmateMember(manager.id(), runningmateId, memberId);

        return ResponseEntity.ok().build();
    }

    // 활동 보고서 관리
    @PatchMapping("{runningmateId}/activities/{activityId}")
    public ResponseEntity<Void> acceptActivityReport(                       // 보고서 승인
            @PathVariable Long runningmateId, @PathVariable Long activityId
    ) {
        return null;
    }

    @DeleteMapping("{runningmateId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivityReport(
            @PathVariable Long runningmateId, @PathVariable Long activityId
    ) {
        return null;
    }
}
