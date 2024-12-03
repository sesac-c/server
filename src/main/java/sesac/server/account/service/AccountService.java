package sesac.server.account.service;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import sesac.server.account.dto.request.EmailCheckRequest;
import sesac.server.account.dto.request.LoginRequest;
import sesac.server.account.dto.request.ResetPasswordRequest;
import sesac.server.account.dto.request.SignupRequest;
import sesac.server.account.dto.request.VerifyCodeRequest;
import sesac.server.account.dto.response.LoginResponse;
import sesac.server.account.dto.response.PasswordResetResponse;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.exception.AccountException;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.util.EmailUtil;
import sesac.server.common.util.JwtUtil;
import sesac.server.common.util.RedisUtil;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
import sesac.server.user.repository.ManagerRepository;
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
    private final EmailUtil emailUtil;
    private final RedisUtil<String> redisUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final ManagerRepository managerRepository;

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

        if (user.getRole() == UserRole.MANAGER) {
            return loginManager(user);
        }

        return loginStudent(user);
    }

    public LoginResponse loginStudent(User user) {
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.NO_EMAIL_OR_PASSWORD));

        switch (student.getStatusCode()) {
            case 0:
                throw new AccountException(AccountErrorCode.PENDING_ACCOUNT);
            case 20:
                throw new AccountException(AccountErrorCode.HOLD_ACCOUNT);
            case 30:
                throw new AccountException(AccountErrorCode.REJECTED_ACCOUNT);
        }

        Map<String, Object> claims = createTokenClaims(user, student.getNickname());

        return createLoginResponse(claims, user.getRole(), student.getNickname(),
                student.getProfile());
    }

    public LoginResponse loginManager(User user) {
        Manager manager = managerRepository.findById(user.getId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.NO_EMAIL_OR_PASSWORD));

        Map<String, Object> claims = createTokenClaims(user, manager.getCampus().getName());

        return createLoginResponse(claims, user.getRole(), manager.getCampus().getName(),
                manager.getProfile());
    }

    private LoginResponse createLoginResponse(Map<String, Object> claims, UserRole role,
            String nickname, String profile) {

        return new LoginResponse(
                jwtUtil.generateToken(claims, 1),
                jwtUtil.generateToken(claims, 14),
                nickname,
                role,
                profile
        );
    }

    private static Map<String, Object> createTokenClaims(User user, String nickname) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", user.getId().toString());
        claims.put("role", user.getRole());
        claims.put("nickname", nickname);
        return claims;
    }

    public void logout(String token) {
        Map<String, Object> claims = jwtUtil.validateToken(token);
        Long expirationTime = (Long) claims.get("exp");

        tokenBlacklistService.addToBlacklist(token, expirationTime);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public PasswordResetResponse checkEmailAndGenerateCode(EmailCheckRequest request)
            throws Exception {
        String email = request.email();
        boolean exists = userRepository.existsByEmail(email);

        if (!exists) {                                                      // 이메일 존재하지 않음
            return PasswordResetResponse.emailVerificationFailure();
        }

        String code = createAuthenticationCode();                           // 인증번호 생성, 저장
        redisUtil.setValue(getPasswordResetCodeKey(email), code, Duration.ofMinutes(3));

        sendCode(email, code);                                              // 이메일 전송(비동기)

        return PasswordResetResponse.emailVerificationSuccess();
    }

    public PasswordResetResponse validateCodeAndGeneratePasswordResetUrl(VerifyCodeRequest request)
            throws Exception {
        String email = request.email();
        String code = request.code();
        String redisEmailKey = getPasswordResetCodeKey(email);

        boolean isCodeVerified = redisUtil.isValueEqual(redisEmailKey, code);
        if (!isCodeVerified) {                                               // 인증번호가 불일치
            return PasswordResetResponse.codeVerificationFailure();
        }

        redisUtil.deleteValue(redisEmailKey);                                // 인증 코드 삭제

        String uuid = UUID.randomUUID().toString();                         // url의 uuid 생성, 저장
        redisUtil.setValue(getPasswordResetUuidKey(uuid), email);

        return PasswordResetResponse.codeVerificationSuccess(uuid);
    }

    public PasswordResetResponse validateResetPageUuid(String uuid) {

        String redisUuidKey = getPasswordResetUuidKey(uuid);

        try {
            redisUtil.getValue(redisUuidKey);
        } catch (Exception e) {                                               // 없는 uuid
            return PasswordResetResponse.uuidVerificationFailure();
        }

        return PasswordResetResponse.uuidVerificationSuccess();
    }

    public void updatePassword(ResetPasswordRequest request) throws Exception {

        String redisUuidKey = getPasswordResetUuidKey(request.uuid());
        String email;
        try {
            email = redisUtil.getValue(redisUuidKey);                   // 없는 uuid는 throw
        } catch (Exception e) {
            throw e;
        }

        User user = userRepository.findByEmail(email)                   // value인 email로 user get
                .orElseThrow(() -> new AccountException(AccountErrorCode.EMAIL_NOT_FOUND));

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            // 이전 비밀번호와 같은지 확인
            throw new AccountException(AccountErrorCode.PASSWORD_SAME_AS_PREVIOUS);
        }
        user.updatePassword(passwordEncoder.encode(request.password()));// 비밀번호 업데이트

        redisUtil.deleteValue(redisUuidKey);                            // 재설정 페이지 삭제
    }


    private String createAuthenticationCode() {
        return RandomStringUtils.random(10, true, true);  // 인증번호 생성
    }

    private String getPasswordResetCodeKey(String email) {
        return "password_reset_code_" + email;
    }

    private String getPasswordResetUuidKey(String uuid) {
        return "password_reset_uuid_" + uuid;
    }

    private void sendCode(String email, String code) {
        String subject = "[SeSACC] 인증번호를 안내해 드립니다.";
        String templateName = "email/find-password_send-code";

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss");
        String formattedCurrentTime = currentTime.format(formatter);

        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("currentTime", formattedCurrentTime);

        emailUtil.sendTemplateEmail(email, subject, templateName, context);
    }

}