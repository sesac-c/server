package sesac.server.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final StudentRepository studentRepository;
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;

    public List<StudentListResponse> getSearchStudentList(String nickname) {
        List<Student> studentList = studentRepository.findByNicknameContainingIgnoreCase(nickname);

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

        return new PageResponse<>(students);
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
}
