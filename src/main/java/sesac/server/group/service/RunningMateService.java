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
import sesac.server.group.dto.request.CreateActivityReportRequest;
import sesac.server.group.dto.request.CreateRunningMateMemberRequest;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateMemberRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.ActivityReportDetailResponse;
import sesac.server.group.dto.response.ActivityReportListResponse;
import sesac.server.group.dto.response.ActivityReportMembers;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberListResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.ActivityParticipant;
import sesac.server.group.entity.ActivityReport;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.entity.RunningMateMember.MemberRole;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.group.repository.ActivityParticipantRepository;
import sesac.server.group.repository.ActivityReportRepository;
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
    private final ActivityReportRepository activityReportRepository;
    private final ActivityParticipantRepository activityParticipantRepository;

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

    public RunningMateDetailResponse runningmate(Long userId) {
        RunningMate runningMate = runningmateRepository.findByUserId(userId).orElseThrow(
                () -> new BaseException(RunningMateErrorCode.NO_RUNNINGMATE)
        );

        return RunningMateDetailResponse.from(runningMate);
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
                .findByUserIdAndRunningMateId(memberId, runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        return RunningMateMemberDetailResponse.from(runningMateMember);
    }

    public Long updateRunningmateMember(Long managerId, Long runningmateId, Long memberId,
            UpdateRunningMateMemberRequest request
    ) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        RunningMateMember runningMateMember = runningMateMemberRepository
                .findByUserIdAndRunningMateId(memberId, runningmateId)
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
                .findByUserIdAndRunningMateId(memberId, runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        if (!runningMateMember.getRunningMate().getCourse().getCampus().getId()
                .equals(manager.getCampus().getId())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningMateMemberRepository.delete(runningMateMember);
    }

    public Long createActivityReport(Long userId,
            CreateActivityReportRequest request) {
        Long runningmateId = runningMateMemberRepository.findRunningMateId(userId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        RunningMate runningMate = runningmateRepository.findById(runningmateId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        ActivityReport report = request.toEntity(runningMate);
        activityReportRepository.save(report);

        // save activity participant
        List<RunningMateMember> members = runningMateMemberRepository
                .findByRunningMateIdAndUserIds(runningmateId, request.memberIds());

        List<ActivityParticipant> participants = members.stream()
                .map(member -> ActivityParticipant.builder()
                        .activityReport(report)
                        .runningMateMember(member)
                        .build()
                ).toList();

        activityParticipantRepository.saveAll(participants);

        return report.getId();
    }

    public List<ActivityReportListResponse> getActivityReportList(Long userId, Pageable pageable) {
        Long runningmateId = runningMateMemberRepository.findRunningMateId(userId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE));

        return activityReportRepository.findList(runningmateId, pageable);
    }

    public ActivityReportDetailResponse getActivityReport(Long activityId) {
        ActivityReport report = activityReportRepository.findById(activityId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_ACTIVITY_REPORT));

        RunningMate runningMate = report.getRunningMate();

        List<String> participants = activityParticipantRepository.findParticipants(report.getId());
        List<ActivityReportMembers> members = runningMateMemberRepository.findActivityReportMembers(
                runningMate.getId());

        ActivityReportDetailResponse response = ActivityReportDetailResponse.builder()
                .runningMateName(runningMate.getName())
                .runningMateGoal(runningMate.getGoal())
                .runningMateSubject(runningMate.getSubject())
                .activityDuration(report.getActivityDuration())
                .activityAt(report.getCreatedAt().toLocalDate())
                .mainContent(report.getMainContent())
                .achievementSummary(report.getAchievementSummary())
                .photo(report.getPhoto())
                .campusName(runningMate.getCourse().getCampus().getName())
                .courseName(runningMate.getCourse().getName())
                .startDate(runningMate.getCourse().getStartDate())
                .endDate(runningMate.getCourse().getEndDate())
                .members(members)
                .participants(participants)
                .build();
        return response;
    }

    public void transformLeader(Long leaderId, Long memberId) {
        RunningMateMember leader = runningMateMemberRepository.findByUserId(leaderId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        RunningMateMember member = runningMateMemberRepository.findById(memberId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        if (!leader.getRole().equals(MemberRole.LEADER)) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        leader.setRole(MemberRole.MEMBER);
        member.setRole(MemberRole.LEADER);

        runningMateMemberRepository.save(member);
        runningMateMemberRepository.save(leader);

    }

    public void deleteMember(Long leaderId, Long memberId) {
        RunningMateMember leader = runningMateMemberRepository.findByUserId(leaderId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        RunningMateMember member = runningMateMemberRepository.findByUserId(memberId)
                .orElseThrow(() -> new BaseException(RunningMateErrorCode.NO_RUNNING_MATE_MEMBER));

        if (!leader.getRole().equals(MemberRole.LEADER)) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        runningMateMemberRepository.delete(member);

    }
}
