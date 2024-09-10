package sesac.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

@SpringBootTest
@Transactional
@Log4j2
class UserServiceTest {

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    Campus campus1;
    Campus campus2;

    Course course1;
    Course course2;

    @BeforeEach
    public void setup() {
        campus1 = Campus.builder()
                .name("영등포")
                .address("영등포 영영")
                .build();

        campus2 = Campus.builder()
                .name("금천")
                .address("금천 금금")
                .build();

        em.persist(campus1);
        em.persist(campus2);

        LocalDate now = LocalDate.now();
        course1 = Course.builder()
                .campus(campus1)
                .name("영등포 자바")
                .classNumber("과정 1")
                .instructorName("자바샘")
                .startDate(now)
                .endDate(now.plusMonths(1))
                .build();

        course2 = Course.builder()
                .campus(campus2)
                .name("금천 파이썬")
                .classNumber("과정 2")
                .instructorName("파샘")
                .startDate(now)
                .endDate(now.plusMonths(1))
                .build();

        em.persist(course1);
        em.persist(course2);

    }

    @Nested
    class CrudTest {

        User studentUser1;
        User studentUser2;
        Student student1;
        Student student2;

        User managerUser1;
        User managerUser2;

        Manager manager1;
        Manager manager2;

        @BeforeEach
        public void crudSetup() {
            studentUser1 = User.builder()
                    .email("user1@example.com")
                    .role(UserRole.STUDENT)
                    .password("1234")
                    .build();

            studentUser2 = User.builder()
                    .email("user2@example.com")
                    .role(UserRole.STUDENT)
                    .password("1234")
                    .build();

            em.persist(studentUser1);
            em.persist(studentUser2);

            student1 = Student.builder()
                    .user(studentUser1)
                    .name("일학생")
                    .birthDate(LocalDate.parse("19990101", DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .firstCourse(course1)
                    .gender('M')
                    .nickname("일학생")
                    .statusCode(10)
                    .build();

            student2 = Student.builder()
                    .user(studentUser2)
                    .name("이학생")
                    .birthDate(LocalDate.parse("19990101", DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .firstCourse(course2)
                    .gender('W')
                    .nickname("이학생")
                    .statusCode(10)
                    .build();

            em.persist(student1);
            em.persist(student2);

            managerUser1 = User.builder()
                    .email("manager1@example.com")
                    .role(UserRole.MANAGER)
                    .password("1234")
                    .build();

            managerUser2 = User.builder()
                    .email("manager2@example.com")
                    .role(UserRole.MANAGER)
                    .password("1234")
                    .build();

            em.persist(managerUser1);
            em.persist(managerUser2);

            manager1 = Manager.builder()
                    .user(managerUser1)
                    .campus(campus1)
                    .build();

            manager2 = Manager.builder()
                    .user(managerUser2)
                    .campus(campus2)
                    .build();

            em.persist(manager1);
            em.persist(manager2);

            em.flush();
            em.clear();
        }

        @Test
        @DisplayName("학생 조회 테스트")
        public void getDetailStudent() {
            // when
            StudentDetailResponse response = userService.getStudent(studentUser1.getId());

            // then
            assertThat(response.nickname()).isEqualTo(student1.getNickname());
            assertThat(response.campus().getId()).isEqualTo(campus1.getId());
            assertThat(response.course().getId()).isEqualTo(course1.getId());
        }

        @Test
        @DisplayName("학생 수정 테스트")
        public void updateStudent() {
            // give
            UpdateStudentRequest request = new UpdateStudentRequest("일학생수정", "일학생수정", null, null);

            // when
            Long updatedId = userService.updateStudent(manager1.getId(), student1.getId(), request);
            em.flush();
            em.clear();

            // then
            Student student = em.find(Student.class, updatedId);
            assertThat(student.getNickname()).isEqualTo("일학생수정");
            assertThat(student.getName()).isEqualTo("일학생수정");
        }

        @Test
        @DisplayName("다른 캠퍼스 학생 수정 테스트")
        public void updateOtherCampusStudent() {
            // give
            UpdateStudentRequest request = new UpdateStudentRequest("이학생수정", "이학생수정", null, null);

            // when
            BaseException ex = Assertions.assertThrows(BaseException.class,
                    () -> userService.updateStudent(manager1.getId(), student2.getId(), request));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(GlobalErrorCode.NO_PERMISSIONS);
        }

        @Test
        @DisplayName("학생 승인 테스트")
        public void acceptStudent() {
            // give
            AcceptStatusRequest request = new AcceptStatusRequest(20, "그냥");

            // when
            Long updatedId = userService.acceptStudent(manager1.getId(), student1.getId(), request);
            em.flush();
            em.clear();

            // then
            Student student = em.find(Student.class, updatedId);
            assertThat(student.getStatusCode()).isEqualTo(20);
        }

        @Test
        @DisplayName("학생 승인 null 테스트")
        public void acceptStatusNullStudent() {
            // give
            AcceptStatusRequest request = new AcceptStatusRequest(null, null);

            // when
            Long updatedId = userService.acceptStudent(manager1.getId(), student1.getId(), request);
            em.flush();
            em.clear();

            // then
            Student student = em.find(Student.class, updatedId);
            assertThat(student.getStatusCode()).isEqualTo(10);
        }

        @Test
        @DisplayName("다른 캠퍼스 학생 승인 테스트")
        public void acceptOtherCampusStudent() {
            // give
            AcceptStatusRequest request = new AcceptStatusRequest(20, "그냥");

            // when
            BaseException ex = Assertions.assertThrows(BaseException.class,
                    () -> userService.acceptStudent(manager1.getId(), student2.getId(), request));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(GlobalErrorCode.NO_PERMISSIONS);
        }
    }

    @Nested
    @DisplayName("리스트 테스트")
    class ListTest {

        @BeforeEach
        public void listSetup() {
            String[] names = {"가", "가다가", "가나", "나", "다", "라", "마", "바", "사", "아", "자", "차"};

            for (int i = 1; i <= 12; i++) {
                User user = User.builder()
                        .email("user" + i + "@example.com")
                        .role(UserRole.STUDENT)
                        .password("1234")
                        .build();

                em.persist(user);

                Student student = Student.builder()
                        .user(user)
                        .name(names[i - 1])
                        .birthDate(LocalDate.parse("19990101",
                                DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .firstCourse(i % 2 == 1 ? course1 : course2)
                        .gender('M')
                        .nickname(names[i - 1])
                        .statusCode((i / 10 + 1) * 10)
                        .build();

                em.persist(student);
            }
        }

        @Test
        @DisplayName("페이지 = 0, 사이즈 = 10, 검색어 x")
        public void listTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(10);
        }

        @Test
        @DisplayName("페이지 = 1, 사이즈 = 10, 검색어 x")
        public void listPagingTest() {
            // give
            PageRequest pageRequest = PageRequest.of(1, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(2);
        }

        @Test
        @DisplayName("이름 검색")
        public void listSearchNameTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, "가", null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(3);
        }

        @Test
        @DisplayName("과정 검색")
        public void listSearchCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest("영등포", null, null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(6);
        }

        @Test
        @DisplayName("이름 + 과정 검색")
        public void listSearchNameCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest("영등포", "가", null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(2);
        }

        @Test
        @DisplayName("승인 여부 검색")
        public void listSearchStatusTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, 10);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(9);
        }

        @Test
        @DisplayName("조회 결과 없음")
        public void listEmptyTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest("파", "하", 30);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students).hasSize(0);
        }

        @Test
        @DisplayName("정렬 = 아이디, 내림차순 (default)")
        public void listSortTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students.get(0).id()).isGreaterThan(students.get(1).id());
        }

        @Test
        @DisplayName("정렬 = 이름, 오름차순")
        public void listSortCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, "name");
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponse<SearchStudentResponse> response =
                    userService.getStudentList(pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.content();
            assertThat(students.stream().map(student -> student.name()).toList())
                    .containsSequence("가", "가나", "가다가", "나", "다", "라", "마", "바", "사", "아");
        }
    }
}