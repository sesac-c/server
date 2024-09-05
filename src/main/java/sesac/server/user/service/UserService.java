package sesac.server.user.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import sesac.server.user.dto.response.ManagerListResponse;
import sesac.server.user.dto.response.StudentListResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final StudentRepository studentRepository;
    private final ManagerRepository managerRepository;

    public List<StudentListResponse> getStudentList(String nickname) {
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
}
