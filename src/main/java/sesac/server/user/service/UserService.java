package sesac.server.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.feed.service.PostService;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdatePasswordRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.ManagerPageResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.dto.response.UserArchiveResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Service
public class UserService extends CommonUserService {

    private final PostService postService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            StudentRepository studentRepository,
            ManagerRepository managerRepository,
            PostService postService,
            PasswordEncoder passwordEncoder
    ) {
        super(userRepository, studentRepository, managerRepository);
        this.postService = postService;
        this.passwordEncoder = passwordEncoder;
    }


    public List<StudentListResponse> getSearchStudentList(String nickname) {
        List<Student> studentList = studentRepository.findByNicknameContainingIgnoreCase(
                nickname);

        List<StudentListResponse> response = studentList.stream().map(StudentListResponse::new)
                .toList();

        return response;
    }

    public List<ManagerListResponse> getManagerList() {
        List<Manager> managerList = managerRepository.findAll();
        List<ManagerListResponse> response = managerList.stream().map(ManagerListResponse::new)
                .toList();

        return response;
    }

    public PageResponse<SearchStudentResponse> getStudentList(
            Long managerId,
            Pageable pageable,
            SearchStudentRequest request
    ) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        Long campusId = manager.getCampus().getId();

        Page<SearchStudentResponse> students =
                studentRepository.searchStudent(campusId, pageable, request);

        return new ManagerPageResponse<>(students, manager.getCampus().getId());
    }

    public StudentDetailResponse getStudent(Long userId) {
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));

        return StudentDetailResponse.from(student);
    }

    public Long updateStudent(Long managerId, Long userId, UpdateStudentRequest request) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));

        if (!student.getFirstCourse().getCampus().equals(manager.getCampus())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        student.update(request);
        studentRepository.save(student);
        return student.getId();
    }

    public Long acceptStudent(Long managerId, Long userId, AcceptStatusRequest request) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));

        if (!student.getFirstCourse().getCampus().equals(manager.getCampus())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        student.setStatus(request);
        studentRepository.save(student);

        return student.getId();
    }

    public void deleteStudent(Long managerId, Long userId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_MANAGER));

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));

        if (!student.getFirstCourse().getCampus().equals(manager.getCampus())) {
            throw new BaseException(GlobalErrorCode.NO_PERMISSIONS);
        }

        User user = student.getUser();
        studentRepository.delete(student);
        userRepository.delete(user);
    }

    public List<StudentListResponse> getCourseUsers(Long courseId) {
        List<Student> students = studentRepository.findByFirstCourseId(courseId);

        return students.stream().map(StudentListResponse::new).collect(Collectors.toList());
    }

    public List<StudentListResponse> getRunningMateUsers(Long runningMateId) {
        List<Student> students = studentRepository.findByRunningMateId(runningMateId);

        return students.stream().map(StudentListResponse::new).collect(Collectors.toList());
    }

    public List<UserArchiveResponse> getUserPosts(Long profileUserId) {
        return postService.getUserPostList(profileUserId).stream().map(UserArchiveResponse::from)
                .toList();
    }

    public List<UserArchiveResponse> getUserLikePosts(CustomPrincipal principal) {
        return postService.getUserLikePostList(principal.id()).stream()
                .map(UserArchiveResponse::from)
                .toList();
    }

    public List<UserArchiveResponse> getUserReplyPosts(CustomPrincipal principal) {
        return postService.getUserReplyPostList(principal.id()).stream()
                .map(UserArchiveResponse::from)
                .toList();
    }

    public Map<String, String> getUserAccountInfo(CustomPrincipal principal) {
        Map<String, String> accountInfo = new HashMap<>();

        User user = getUserOrThrowException(principal.id());

        accountInfo.put("id", String.valueOf(user.getId()));
        accountInfo.put("email", user.getEmail());
        accountInfo.put("birthdate",
                user.getRole().equals(UserRole.MANAGER) ? null : String.valueOf(
                        formatDate(user.getStudent().getBirthDate())));
        return accountInfo;
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        return date.format(formatter);
    }

    public void updatePassword(CustomPrincipal principal, UpdatePasswordRequest request) {
        User user = getUserOrThrowException(principal.id());

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            // 이전 비밀번호와 같은지 확인
            throw new BaseException(UserErrorCode.PASSWORD_SAME_AS_PREVIOUS);
        }
        user.updatePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }
}
