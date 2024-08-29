package sesac.server.account.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sesac.server.account.dto.LoginRequest;
import sesac.server.account.dto.LoginResponse;
import sesac.server.account.dto.SignupRequest;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.exception.AccountException;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.util.JwtUtil;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
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
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public void checkEmail(String email) {
        boolean exits = userRepository.existsByEmail(email);

        if (exits) {
            throw new AccountException(AccountErrorCode.DUPLICATED_EMAIL);
        }
    }

    public Student createStudent(SignupRequest signupRequest) {
        if (!signupRequest.password().equals(signupRequest.passwordConfirm())) {
            throw new AccountException(AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM);
        }

        this.checkEmail(signupRequest.email());

        User user = User.builder()
                .email(signupRequest.email())
                .password(passwordEncoder.encode(signupRequest.password()))
                .role(UserRole.STUDENT)
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
                .statusCode(0)
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

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new AccountException(AccountErrorCode.NO_EMAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AccountException(AccountErrorCode.NO_EMAIL_OR_PASSWORD);
        }

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.NO_EMAIL_OR_PASSWORD));

        if (student.getStatusCode() == 0) {
            throw new AccountException(AccountErrorCode.PENDING_ACCOUNT);
        }

        if (student.getStatusCode() == 20) {
            throw new AccountException(AccountErrorCode.HOLD_ACCOUNT);
        }

        if (student.getStatusCode() == 30) {
            throw new AccountException(AccountErrorCode.REJECTED_ACCOUNT);
        }

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", user.getId().toString());
        claims.put("role", user.getRole());

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 14);

        LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                student.getNickname(),
                user.getRole()
        );

        return response;
    }

    public void logout(String token) {
        Map<String, Object> claims = jwtUtil.validateToken(token);
        Long expirationTime = (Long) claims.get("exp");

        tokenBlacklistService.addToBlacklist(token, expirationTime);
    }
}
