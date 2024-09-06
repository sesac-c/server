package sesac.server.group.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("runningmates")
public class RunningmateController {

    @GetMapping("{runningmateId}/activities")
    public ResponseEntity<Void> getActivityReportList() {
        return null;
    }

    @PostMapping("{runningmateId}/activities")
    public ResponseEntity<Void> createActivityReport() {
        return null;
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
    public ResponseEntity<Void> getRunningmateList() {
        return null;
    }

    @PostMapping
    public ResponseEntity<Void> createRunningmate() {
        return null;
    }

    @GetMapping("{runningmateId}")
    public ResponseEntity<Void> getRunningmate(@PathVariable Long runningmateId) {
        return null;
    }

    @PutMapping("{runningmateId}")
    public ResponseEntity<Void> updateRunningmate(@PathVariable Long runningmateId) {
        return null;
    }

    @DeleteMapping("{runningmateId}")
    public ResponseEntity<Void> deleteRunningmate(@PathVariable Long runningmateId) {
        return null;
    }

    // 멤버 관리
    @GetMapping("{runningmateId}/members")
    public ResponseEntity<Void> getRunningmateMemberList(@PathVariable Long runningmateId) {
        return null;
    }

    @PostMapping("{runningmateId}/members")
    public ResponseEntity<Void> createRunningmateMember(@PathVariable Long runningmateId) {
        return null;
    }

    @GetMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<Void> getRunningmateMember(
            @PathVariable Long runningmateId, @PathVariable Long memberId
    ) {
        return null;
    }

    @PutMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<Void> updateRunningmateMember(
            @PathVariable Long runningmateId, @PathVariable Long memberId
    ) {
        return null;
    }

    @DeleteMapping("{runningmateId}/member/{memberId}")
    public ResponseEntity<Void> deleteRunningmateMember(
            @PathVariable Long runningmateId, @PathVariable Long memberId
    ) {
        return null;
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
