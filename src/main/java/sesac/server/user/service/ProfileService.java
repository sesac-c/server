package sesac.server.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Course;
import sesac.server.campus.service.CourseService;
import sesac.server.common.exception.BaseException;
import sesac.server.user.dto.request.NicknameCheckRequest;
import sesac.server.user.dto.request.UpdateProfileRequest;
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
@RequiredArgsConstructor
public class ProfileService extends CommonUserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;
    private final CourseChangeRequestRepository courseChangeRequestRepository;

    private final CourseService courseService;

    private static final String DEFAULT_PROFILE_IMAGE = "default-profile.png";
    private static final int COURSE_CHANGE_REQUEST_STATUS = 0;

    public ProfileResponse getProfile(CustomPrincipal principal, Long profileUserId) {
        return userRepository.getProfileResponse(profileUserId, principal);
    }

    public boolean isExistNickname(NicknameCheckRequest request) {
        return studentRepository.existsByNickname(request.nickname());
    }

    public String getManagerProfileForm(CustomPrincipal customPrincipal) {
        String profileImage = managerRepository.findProfileImageById(customPrincipal.id());
        if (profileImage == null) {
            profileImage = DEFAULT_PROFILE_IMAGE;
        }
        return profileImage;
    }

    public StudentProfileFormResponse getStudentProfileForm(CustomPrincipal customPrincipal) {
        return studentRepository.getStudentProfileFormResponse(customPrincipal);
    }

    public void updateProfile(CustomPrincipal principal, UpdateProfileRequest request) {
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

    private void updateStudentProfile(Long id, UpdateProfileRequest request) {
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
                COURSE_CHANGE_REQUEST_STATUS)) {
            throw new BaseException(UserErrorCode.EXISTING_CHANGE_REQUEST);
        }
    }

    private void validateRequestedCourseIsDifferent(Long studentId, Long courseId) {
        Student student = getUserOrThrowException(studentRepository, studentId);
        if (courseId == student.getCourse().getId()) {
            throw new BaseException(UserErrorCode.SAME_COURSE_REQUEST);
        }
    }
}
