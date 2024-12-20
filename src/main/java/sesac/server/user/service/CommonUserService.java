package sesac.server.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Campus;
import sesac.server.common.entity.HasCampus;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.ErrorCode;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.exception.UserErrorCode;
import sesac.server.user.repository.ManagerRepository;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Transactional
@RequiredArgsConstructor
public class CommonUserService {

    protected final UserRepository userRepository;
    protected final StudentRepository studentRepository;
    protected final ManagerRepository managerRepository;

    public Campus getUserCampus(CustomPrincipal principal) {
        Long userId = principal.id();
        String userRole = principal.role();
        HasCampus entity =
                switch (userRole) {
                    case "STUDENT" -> getUserOrThrowException(studentRepository, userId);
                    case "MANAGER" -> getUserOrThrowException(managerRepository, userId);
                    default -> throw new IllegalStateException("존재하지 않는 권한입니다: " + userRole);
                };

        return entity.getCampus();
    }


    public Student getStudentOrThrowException(CustomPrincipal principal) {
        return getUserOrThrowException(studentRepository, principal.id());
    }

    public Manager getManagerOrThrowException(CustomPrincipal principal) {
        return getUserOrThrowException(managerRepository, principal.id());
    }

    public <T> T getUserOrThrowException(JpaRepository<T, Long> repository, Long userId,
            ErrorCode errorCode) {
        return repository.findById(userId)
                .orElseThrow(() -> new BaseException(errorCode));
    }

    public <T> T getUserOrThrowException(JpaRepository<T, Long> repository, Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorCode.NO_USER));
    }

    public User getUserOrThrowException(Long userId, ErrorCode errorCode) {
        return getUserOrThrowException(userRepository, userId, errorCode);
    }

    public User getUserOrThrowException(Long userId) {
        return getUserOrThrowException(userRepository, userId, UserErrorCode.NO_USER);
    }

}
