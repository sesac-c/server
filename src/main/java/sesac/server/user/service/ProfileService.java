package sesac.server.user.service;

import static org.springframework.util.StringUtils.hasText;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Course;
import sesac.server.campus.service.CourseService;
import sesac.server.common.constants.AppConstants;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.common.util.EmailUtil;
import sesac.server.user.dto.request.CourseChangeRequestRequest;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.request.UpdateProfileRequest;
import sesac.server.user.dto.response.CourseChangeRequestResponse;
import sesac.server.user.dto.response.ProfileResponse;
import sesac.server.user.dto.response.StudentProfileFormResponse;
import sesac.server.user.entity.CourseChangeRequest;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.CourseChangeRequestRepository;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Service
public class ProfileService extends CommonUserService {

    private final CourseChangeRequestRepository courseChangeRequestRepository;

    private final CourseService courseService;

    private final EmailUtil emailUtil;

    protected ProfileService(UserRepository userRepository,
            StudentRepository studentRepository,
            ManagerRepository managerRepository,
            CourseChangeRequestRepository courseChangeRequestRepository,
            CourseService courseService, EmailUtil emailUtil
    ) {
        super(userRepository, studentRepository, managerRepository);
        this.courseChangeRequestRepository = courseChangeRequestRepository;
        this.courseService = courseService;
        this.emailUtil = emailUtil;
    }

    public ProfileResponse getProfile(CustomPrincipal principal, Long profileUserId) {
        return userRepository.getProfileResponse(profileUserId, principal);
    }

    public boolean isExistNickname(NicknameCheckRequest request) {
        return studentRepository.existsByNickname(request.nickname());
    }

    public String getManagerProfileForm(CustomPrincipal customPrincipal) {
        String profileImage = managerRepository.findProfileImageById(customPrincipal.id());
        if (profileImage == null) {
            profileImage = AppConstants.DEFAULT_PROFILE_IMAGE;
        }
        return profileImage;
    }

    public StudentProfileFormResponse getStudentProfileForm(CustomPrincipal customPrincipal) {
        return studentRepository.getStudentProfileFormResponse(customPrincipal);
    }

    public void updateProfile(CustomPrincipal principal, @Valid UpdateProfileRequest request) {
        String role = principal.role();
        Long id = principal.id();

        switch (role) {
            case "STUDENT" -> updateStudentProfile(id, request);
            case "MANAGER" -> updateManagerProfile(id, request);
            default -> throw new IllegalStateException("존재하지 않는 권한입니다: " + role);
        }
    }

    public void requestChangeCourse(CustomPrincipal principal, Long campusId, Long courseId) {
        // 이미 강의 변경 신청 이력이 있을 경우
        Long studentId = principal.id();
        validateNoPendingCourseChangeRequest(studentId);

        // 신청과 학생의 현재 강의(first course)가 같을 경우
        Student student = getUserOrThrowException(studentRepository, studentId);
        validateRequestedCourseIsDifferent(studentId, courseId);

        Course course = courseService.getCourseByIdAndCampusId(courseId, campusId);
        CourseChangeRequest courseChangeRequest = CourseChangeRequest.builder()
                .student(student)
                .newCourse(course)
                .statusCode(0)
                .build();

        courseChangeRequestRepository.save(courseChangeRequest);
    }

    public PageResponse<CourseChangeRequestResponse> getCourseChangeRequestList(
            Pageable pageable, String status
    ) {
        Page<CourseChangeRequestResponse> responses = courseChangeRequestRepository
                .searchCourseChangeRequests(pageable, status);
        return new PageResponse<>(responses);
    }

    public void processCourseChangeRequest(Long courseChangeRequestId,
            @Valid CourseChangeRequestRequest request) {
        validateCourseChangeRequestRequest(request);
        CourseChangeRequest courseChangeRequest = courseChangeRequestRepository.findById(
                        courseChangeRequestId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_COURSE_CHANGE_REQUEST));

        courseChangeRequest.update(request.status(), request.rejectReason());
        sendCourseChangeRequestResult(courseChangeRequest);
    }

    private void updateStudentProfile(Long id, UpdateProfileRequest request) {
        if (!hasText(request.nickname())) {
            throw new BaseException(UserErrorCode.REQUIRED_NICKNAME);
        }
        if (isNumber(request.nickname())) {
            throw new BaseException(UserErrorCode.INVALID_NICKNAME_COMBINATION);
        }
        Student student = getUserOrThrowException(studentRepository, id);
        student.updateProfile(request);
        studentRepository.save(student);
    }

    private void updateManagerProfile(Long id, UpdateProfileRequest request) {
        Manager manager = getUserOrThrowException(managerRepository, id);
        manager.updateProfile(request);
        managerRepository.save(manager);
    }


    private void validateNoPendingCourseChangeRequest(Long studentId) {
        if (courseChangeRequestRepository.existsByStudentIdAndStatusCode(studentId,
                AppConstants.COURSE_CHANGE_REQUEST_STATUS)) {
            throw new BaseException(UserErrorCode.EXISTING_COURSE_CHANGE_REQUEST);
        }
    }

    private void validateRequestedCourseIsDifferent(Long studentId, Long courseId) {
        Student student = getUserOrThrowException(studentRepository, studentId);
        if (courseId == student.getCourse().getId()) {
            throw new BaseException(UserErrorCode.SAME_COURSE_CHANGE_REQUEST);
        }
    }

    private void validateCourseChangeRequestRequest(CourseChangeRequestRequest request) {
        int status = request.status();
        String rejectReason = request.rejectReason();

        if (status != 10 && status != 20 && status != 30) {
            throw new BaseException(UserErrorCode.INVALID_STATUS_CODE);
        }
        if (status != 10 && !hasText(rejectReason)) {
            throw new BaseException(UserErrorCode.NO_COURSE_CHANGE_REQUEST_REJECT_REASON);
        }
    }

    private void sendCourseChangeRequestResult(CourseChangeRequest courseChangeRequest) {
        String email = courseChangeRequest.getStudent().getUser().getEmail();
        String subject = "[SeSACC] 강의 변경 신청 결과를 알려드립니다.";
        String templateName = "email/course-change-request_result";

        Context context = new Context();
        context.setVariable("currentTime",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss")));
        context.setVariable("newCourse", courseChangeRequest.getNewCourse().getName());
        context.setVariable("status", getStatusString(courseChangeRequest.getStatusCode()));
        context.setVariable("rejectResult", courseChangeRequest.getRejectReason());

        emailUtil.sendTemplateEmail(email, subject, templateName, context);
    }

    private String getStatusString(int statusCode) {
        return switch (statusCode) {
            case 10 -> "승인";
            case 20 -> "보류";
            default -> "거절";
        };
    }

    private boolean isNumber(String nickname) {
        return nickname.matches("\\d+");
    }
}
