package sesac.server.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import sesac.server.account.dto.request.LoginRequest;
import sesac.server.account.dto.request.SignupRequest;
import sesac.server.account.dto.response.LoginResponse;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.exception.AccountException;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.CourseRepository;
import sesac.server.common.Fixture;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;
import sesac.server.user.repository.StudentRepository;

@SpringBootTest
@Transactional
@Log4j2
class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    private Course course;
    private Campus campus;

    @BeforeEach
    public void setUp() {
        Fixture.em = em;
        campus = Fixture.createCampus("캠퍼스 1");
        course = Fixture.createCourse("과정 1", campus);
    }

    @Nested
    @DisplayName("계정 생성 테스트")
    class AccountCreateTest {

        @BeforeEach
        public void setup() {
            User studentUser = User.builder()
                    .email("test1@example.com")
                    .password("1234")
                    .role(UserRole.STUDENT)
                    .build();

            User managerUser = User.builder()
                    .email("manager@example.com")
                    .password("1234")
                    .role(UserRole.MANAGER)
                    .build();

            em.persist(studentUser);
            em.persist(managerUser);
            em.flush();
            em.clear();
        }

        @Test
        @DisplayName("이메일 중복 체크 통과")
        public void emailCheck1() {
            accountService.checkEmail("test2@example.com");
        }

        @Test
        @DisplayName("이메일 중복 체크 실패")
        public void emailCheck2() {
            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.checkEmail("test1@example.com"));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.DUPLICATED_EMAIL);
            assertThat(ex.getMessage()).isEqualTo(AccountErrorCode.DUPLICATED_EMAIL.getMessage());
        }

        @Test
        @DisplayName("회원 가입 정상")
        public void signup1() {
            SignupRequest signupRequest = new SignupRequest(
                    "test2@example.com",
                    "asdf1234!",
                    "asdf1234!",
                    "김학생",
                    "990101",
                    1,
                    course.getId()
            );

            Student created = accountService.createStudent(signupRequest);
            Student student = studentRepository.findById(created.getId()).orElse(null);

            assertThat(student).isNotNull();
            assertThat(student.getBirthDate()).isEqualTo(
                    LocalDate.parse("19990101", DateTimeFormatter.ofPattern("yyyyMMdd")));
            assertThat(student.getName()).isEqualTo("김학생");
            assertThat(student.getGender()).isEqualTo('M');
            assertThat(student.getUser().getEmail()).isEqualTo("test2@example.com");
            assertThat(passwordEncoder.matches("asdf1234!",
                    student.getUser().getPassword())).isEqualTo(
                    true);
        }

        @Test
        @DisplayName("비밀번호 확인이 다른 경우")
        public void differentPasswordConfirm() {
            SignupRequest signupRequest = new SignupRequest(
                    "test2@example.com",
                    "asdf1234!",
                    "asdf1234!2",
                    "김학생",
                    "990101",
                    1,
                    course.getId()
            );

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.createStudent(signupRequest));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.DIFFERENT_PASSWORD_CONFIRM);
        }

        @Test
        @DisplayName("과정이 없는 경우")
        public void noCourse() {
            SignupRequest signupRequest = new SignupRequest(
                    "test2@example.com",
                    "asdf1234!",
                    "asdf1234!",
                    "김학생",
                    "990101",
                    1,
                    999999L
            );

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.createStudent(signupRequest));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.NO_COURSE);
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        Student student;
        Manager manager;

        @BeforeEach
        public void setup() {
            student = Fixture.createStudent("학생1", course, 10);
            manager = Fixture.createManager("매니저1", campus);
        }

        @Test
        public void userLogin() {
            LoginRequest request = new LoginRequest("학생1", "1234");

            LoginResponse response = accountService.login(request);

            assertThat(response.role()).isEqualTo(UserRole.STUDENT);
        }

        @Test
        public void userLoginNotFound() {
            LoginRequest request = new LoginRequest("학생2", "1234");

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.login(request));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.NO_EMAIL_OR_PASSWORD);
        }

        @Test
        @DisplayName("미승인 테스트")
        public void userStatusPending() {
            Fixture.createStudent("미승인", course, 0);
            LoginRequest request = new LoginRequest("미승인", "1234");

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.login(request));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.PENDING_ACCOUNT);
        }

        @Test
        @DisplayName("승인보류 테스트")
        public void userStatusHold() {
            Fixture.createStudent("승인보류", course, 20);
            LoginRequest request = new LoginRequest("승인보류", "1234");

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.login(request));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.HOLD_ACCOUNT);
        }

        @Test
        @DisplayName("승인거절 테스트")
        public void userStatusRejected() {
            Fixture.createStudent("승인거절", course, 30);
            LoginRequest request = new LoginRequest("승인거절", "1234");

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.login(request));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.REJECTED_ACCOUNT);
        }

        @Test
        public void managerLogin() {
            LoginRequest request = new LoginRequest("매니저1", "1234");

            LoginResponse response = accountService.login(request);

            assertThat(response.role()).isEqualTo(UserRole.MANAGER);
        }

        @Test
        public void managerLoginNotFound() {
            LoginRequest request = new LoginRequest("매니저", "1234");

            AccountException ex =
                    assertThrows(AccountException.class,
                            () -> accountService.login(request));

            assertThat(ex.getErrorCode()).isEqualTo(AccountErrorCode.NO_EMAIL_OR_PASSWORD);
        }

    }

}