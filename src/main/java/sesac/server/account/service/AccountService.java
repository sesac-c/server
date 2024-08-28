package sesac.server.account.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesac.server.account.dto.SignupRequest;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.exception.AccountException;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserType;
import sesac.server.user.repository.StudentRepository;
import sesac.server.user.repository.UserRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public void emailCheck(String email) {
        boolean exits = userRepository.existsByEmail(email);

        if (exits) {
            throw new AccountException(AccountErrorCode.DUPLICATED_EMAIL);
        }
    }

    public Student saveStudent(SignupRequest signupRequest) {
        if (!signupRequest.password().equals(signupRequest.passwordConfirm())) {
            throw new AccountException(AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM);
        }

        this.emailCheck(signupRequest.email());

        User user = User.builder()
                .email(signupRequest.email())
                .password(passwordEncoder.encode(signupRequest.password()))
                .type(UserType.STUDENT)
                .build();
        userRepository.save(user);

        // 과정 조회, 없으면 에러메시지 반환
        Course course = courseRepository.findById(signupRequest.firstCourseId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.NO_COURSE));

        char gender = genderConvert(signupRequest.gender());
        LocalDate birthDate = birthDateConvert(signupRequest.birthDate(), signupRequest.gender());

        Student student = Student.builder()
                .birthDate(birthDate)
                .name(signupRequest.name())
                .gender(gender)
                .firstCourse(course)
                .statusCode(100)
                .user(user)
                .nickname("새싹_" + user.getId())
                .build();

        studentRepository.save(student);

        return student;
    }

    private char genderConvert(int gender) {
        return gender % 2 == 1 ? 'M' : 'F';
    }

    private LocalDate birthDateConvert(String birthDate, int gender) {
        String birthDateStr = (gender <= 2 ? "19" : "20") + birthDate;
        return LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
