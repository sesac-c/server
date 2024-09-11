package sesac.server.group.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.campus.entity.Course;
import sesac.server.campus.exception.CourseErrorCode;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.group.dto.request.CreateRunningMateMemberRequest;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.group.repository.RunningMateMemberRepository;
import sesac.server.group.repository.RunningMateRepository;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.UserRepository;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class RunningMateService {

    private final RunningMateRepository runningmateRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RunningMateMemberRepository runningMateMemberRepository;

    // 러닝메이트 관리
    public PageResponse<SearchRunningMateResponse> getRunningmateList(Pageable pageable,
            SearchRunningMateRequest request) {
        Page<SearchRunningMateResponse> responses =
                runningmateRepository.runningMateSearch(pageable, request);
        return new PageResponse(responses);
    }

    public Long createRunningmate(CreateRunningMateRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new BaseException(CourseErrorCode.NO_COURSE));

        RunningMate runningMate = request.toEntity(course);
        runningmateRepository.save(runningMate);
        return runningMate.getId();
    }

    public RunningMateDetailResponse getRunningmate(Long runningmateId) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        return RunningMateDetailResponse.from(runningMate);
    }

    public Long updateRunningmate(Long runningmateId, UpdateRunningMateRequest request) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        runningMate.update(request);
        runningmateRepository.save(runningMate);
        return runningMate.getId();
    }

    public void deleteRunningmate(Long runningmateId) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        runningmateRepository.delete(runningMate);
    }

    // 멤버 관리
    //    @GetMapping("{runningmateId}/members")
    //    public ResponseEntity<Void> getRunningmateMemberList(@PathVariable Long runningmateId) {
    //        return null;
    //    }
    //

    public Long createRunningmateMember(Long runningmateId,
            CreateRunningMateMemberRequest request) {
        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));

        boolean exists = runningMateMemberRepository.existsMember(runningmateId, user.getId(),
                request.phoneNumber());

        if (exists) {
            throw new BaseException(RunningMateErrorCode.ALREADY_REGISTERED);
        }

        RunningMateMember runningMateMember = request.toEntity(user, runningMate);
        runningMateMemberRepository.save(runningMateMember);

        return runningMateMember.getId();
    }

//    public RunningMateMember getRunningmateMember(
//            Long runningmateId, Long memberId
//    ) {
//        return null;
//    }
    //
    //    @PutMapping("{runningmateId}/member/{memberId}")
    //    public ResponseEntity<Void> updateRunningmateMember(
    //            @PathVariable Long runningmateId, @PathVariable Long memberId
    //    ) {
    //        return null;
    //    }
    //
    //    @DeleteMapping("{runningmateId}/member/{memberId}")
    //    public ResponseEntity<Void> deleteRunningmateMember(
    //            @PathVariable Long runningmateId, @PathVariable Long memberId
    //    ) {
    //        return null;
    //    }
}
