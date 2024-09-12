package sesac.server.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static sesac.server.common.Fixture.createManager;
import static sesac.server.common.Fixture.createStudent;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
import sesac.server.common.Fixture;
import sesac.server.common.dto.PageResponseDto;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;
import sesac.server.user.dto.request.AcceptStatusRequest;
import sesac.server.user.dto.request.SearchStudentRequest;
import sesac.server.user.dto.request.UpdateStudentRequest;
import sesac.server.user.dto.response.SearchStudentResponse;
import sesac.server.user.dto.response.StudentDetailResponse;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.exception.UserErrorCode;

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

    @BeforeEach
    public void setup() {
        Fixture.em = em;
        campus1 = Fixture.createCampus("영등포");
        campus2 = Fixture.createCampus("금천");
    }

    @Nested
    class CrudTest {

        Course course1;
        Course course2;

        Student student1;
        Student student2;

        Manager manager1;
        Manager manager2;

        @BeforeEach
        public void crudSetup() {
            course1 = Fixture.createCourse("영등포 자바", campus1);
            course2 = Fixture.createCourse("금천 자바", campus2);

            student1 = createStudent("user1", course1, 10);
            student2 = createStudent("user2", course2, 10);

            manager1 = createManager("manager1", campus1);
            manager2 = createManager("manager2", campus2);
        }

        @Test
        @DisplayName("학생 조회 테스트")
        public void getDetailStudent() {
            // when
            StudentDetailResponse response = userService.getStudent(student1.getId());

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
        @DisplayName("학생 승인 거절 테스트(실패)")
        public void acceptRejectStudent() {
            // give
            AcceptStatusRequest request = new AcceptStatusRequest(20, null);

            // when
            BaseException ex = Assertions.assertThrows(BaseException.class,
                    () -> userService.acceptStudent(manager1.getId(), student1.getId(), request));

            // then
            // 보류 또는 거절 시 거절사유 필수 입력
            assertThat(ex.getErrorCode()).isEqualTo(UserErrorCode.REQUIRED_REJECT_REASON);
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

        Course course1;
        Course course2;
        Course course3;

        Manager manager1;
        Manager manager2;

        @BeforeEach
        public void listSetup() {
            // course
            course1 = Fixture.createCourse("영등포 자바", campus1);
            course2 = Fixture.createCourse("영등포 파이선", campus1);
            course3 = Fixture.createCourse("금천 자바", campus2);

            // manager
            manager1 = createManager("manager1", campus1);
            manager2 = createManager("manager2", campus2);

            // campus1 course1 학생
            createStudent("1", course1, 10);
            createStudent("2", course1, 10);
            createStudent("3", course1, 10);
            createStudent("4", course1, 20);

            // campus1 course2 학생
            createStudent("9", course2, 10);
            createStudent("10", course2, 10);
            createStudent("11", course2, 10);
            createStudent("12", course2, 20);

            // campus2 course3 학생
            createStudent("5", course3, 10);
            createStudent("6", course3, 10);
            createStudent("7", course3, 10);
            createStudent("8", course3, 20);
        }

        @Test
        @DisplayName("같은 캠퍼스 소속 학생만 조회 가능, course1 + course2")
        public void listTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when 매니저가 속한 캠퍼스의 학생만 조회 가능
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(8);
        }

        @Test
        @DisplayName("과정 필터링, course1")
        public void listSearchNameTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, course1.getId(), null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(4);
        }

        @Test
        @DisplayName("이름 필터링, 1")
        public void listSearchCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest("1", null, null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(4);
        }

        @Test
        @DisplayName("이름 + 과정 필터링, 1 + course2")
        public void listSearchNameCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest("1", course2.getId(), null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(3);
        }

        @Test
        @DisplayName("승인 여부 필터링, 10")
        public void listSearchStatusTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, 10);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(6);
        }

        @Test
        @DisplayName("다른 캠퍼스 과정은 조회 불가능, course3")
        public void listEmptyTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, course3.getId(), null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students).hasSize(0);
        }

        @Test
        @DisplayName("정렬 = 아이디, 내림차순 (default)")
        public void listSortTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students.get(0).id()).isGreaterThan(students.get(1).id());
        }

        @Test
        @DisplayName("정렬 = 이름, 오름차순")
        public void listSortCourseTest() {
            // give
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, "name");
            SearchStudentRequest request = new SearchStudentRequest(null, null, null);

            // when
            PageResponseDto<SearchStudentResponse> response =
                    userService.getStudentList(manager1.getId(), pageRequest, request);

            // then
            List<SearchStudentResponse> students = response.getContent();
            assertThat(students.stream().map(student -> student.name()).toList())
                    .containsSequence("1", "10", "11", "12", "2", "3", "4", "9");
        }
    }
}