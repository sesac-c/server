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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import sesac.server.account.dto.request.SignupRequest;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.exception.AccountException;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.campus.repository.CourseRepository;
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

    @BeforeEach
    public void setUp() {
        Campus campus = Campus.builder()
                .name("Campus")
                .address("campus address")
                .build();

        em.persist(campus);

        course = Course.builder()
                .campus(campus)
                .name("Course")
                .classNumber("course number")
                .instructorName("instructor name")
                .build();

        em.persist(course);

        User user = User.builder()
                .email("test1@example.com")
                .password("1234")
                .role(UserRole.STUDENT)
                .build();

        em.persist(user);
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
        assertThat(passwordEncoder.matches("asdf1234!", student.getUser().getPassword())).isEqualTo(
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