package sesac.server.group.service;

import jakarta.transaction.Transactional;
import java.util.List;
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
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.group.dto.request.CreateRunningMateMemberRequest;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateMemberRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberListResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.group.repository.RunningMateMemberRepository;
import sesac.server.group.repository.RunningMateRepository;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
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
    private final ManagerRepository managerRepository;

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

    public Long updateRunningmate(Long managerId, Long runningmateId,
            UpdateRunningMateRequest request) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        if (!runningMate.getCourse().getCampus().getId().equals(manager.getCampus().getId())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningMate.update(request);
        runningmateRepository.save(runningMate);
        return runningMate.getId();
    }

    public void deleteRunningmate(Long managerId, Long runningmateId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        if (!runningMate.getCourse().getCampus().getId().equals(manager.getCampus().getId())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningmateRepository.delete(runningMate);
    }

    public List<RunningMateMemberListResponse> getRunningmateMemberList(Long runningmateId) {
        return runningMateMemberRepository.runningMateMembers(runningmateId);
    }

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

    public RunningMateMemberDetailResponse getRunningmateMember(
            Long runningmateId, Long memberId
    ) {
        RunningMateMember runningMateMember = runningMateMemberRepository
                .findByIdAndRunningMateId(memberId, runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        return RunningMateMemberDetailResponse.from(runningMateMember);
    }

    public Long updateRunningmateMember(Long managerId, Long runningmateId, Long memberId,
            UpdateRunningMateMemberRequest request
    ) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        RunningMateMember runningMateMember = runningMateMemberRepository
                .findByIdAndRunningMateId(memberId, runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        if (!runningMateMember.getRunningMate().getCourse().getCampus().getId()
                .equals(manager.getCampus().getId())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningMateMember.update(request);
        runningMateMemberRepository.save(runningMateMember);
        return runningMateMember.getId();
    }

    public void deleteRunningmateMember(
            Long managerId, Long runningmateId, Long memberId
    ) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        RunningMateMember runningMateMember = runningMateMemberRepository
                .findByIdAndRunningMateId(memberId, runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        if (!runningMateMember.getRunningMate().getCourse().getCampus().getId()
                .equals(manager.getCampus().getId())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningMateMemberRepository.delete(runningMateMember);
    }
}
