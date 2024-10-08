package sesac.server.group.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
import sesac.server.group.dto.request.CreateRunningMateMemberRequest;
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateMemberRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberDetailResponse;
import sesac.server.group.dto.response.RunningMateMemberListResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;
import sesac.server.group.entity.RunningMateMember;
import sesac.server.group.entity.RunningMateMember.MemberRole;
import sesac.server.group.exception.RunningMateErrorCode;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;
import sesac.server.user.entity.User;
import sesac.server.user.entity.UserRole;

@SpringBootTest
@Transactional
@Log4j2
class RunningMateServiceTest {

    @Autowired
    RunningMateService runningMateService;

    @PersistenceContext
    EntityManager em;

    Campus campus;
    Course course;

    User managerUser;
    Manager manager;

    @BeforeEach
    void setUp() {
        campus = Campus.builder()
                .name("영등포")
                .address("영등포 영영")
                .build();

        course = Course.builder()
                .campus(campus)
                .name("영등포 자바")
                .classNumber("과정1")
                .instructorName("자바샘")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();

        managerUser = User.builder()
                .email("manager@example.com")
                .role(UserRole.MANAGER)
                .password("1234")
                .build();

        manager = Manager.builder()
                .user(managerUser)
                .campus(campus)
                .build();

        em.persist(campus);
        em.persist(course);
        em.persist(managerUser);
        em.persist(manager);
        em.flush();
        em.clear();
    }


    @Nested
    class CrudTest {

        @Test
        @DisplayName("러닝메이트 생성")
        public void createTest() {
            // give
            CreateRunningMateRequest request = new CreateRunningMateRequest(
                    "리눅스", "리눅스를 배운다", "리눅스 마스터", course.getId());

            // when
            Long createdId = runningMateService.createRunningmate(request);
            em.flush();
            em.clear();

            // then
            RunningMate runningMate = em.find(RunningMate.class, createdId);
            assertThat(runningMate.getName()).isEqualTo("리눅스");
            assertThat(runningMate.getSubject()).isEqualTo("리눅스를 배운다");
        }


        @Test
        @DisplayName("러닝메이트 상세 조회")
        public void getDetailTest() {
            // give
            RunningMate runningMate = RunningMate.builder()
                    .course(course)
                    .name("리눅스")
                    .subject("리눅스를 배운다")
                    .goal("리눅스 마스터")
                    .build();

            em.persist(runningMate);
            em.flush();
            em.clear();

            // when
            RunningMateDetailResponse response = runningMateService.getRunningmate(
                    runningMate.getId());

            // then
            assertThat(response.name()).isEqualTo("리눅스");
            assertThat(response.subject()).isEqualTo("리눅스를 배운다");

        }

        @Test
        @DisplayName("러닝메이트 수정")
        public void updateTest() {
            // give
            RunningMate runningMate = RunningMate.builder()
                    .course(course)
                    .name("리눅스")
                    .subject("리눅스를 배운다")
                    .goal("리눅스 마스터")
                    .build();

            em.persist(runningMate);
            em.flush();
            em.clear();

            UpdateRunningMateRequest request = new UpdateRunningMateRequest("자바를 자바", "자바를 배운다",
                    "자바 마스터");

            // when
            Long updatedId = runningMateService.updateRunningmate(managerUser.getId(),
                    runningMate.getId(), request);
            em.flush();
            em.clear();

            // then
            RunningMate updated = em.find(RunningMate.class, updatedId);
            assertThat(updated.getName()).isEqualTo("자바를 자바");
            assertThat(updated.getSubject()).isEqualTo("자바를 배운다");
        }

        @Test
        @DisplayName("러닝메이트 삭제")
        public void deleteTest() {
            // give
            RunningMate runningMate = RunningMate.builder()
                    .course(course)
                    .name("리눅스")
                    .subject("리눅스를 배운다")
                    .goal("리눅스 마스터")
                    .build();

            em.persist(runningMate);
            em.flush();
            em.clear();

            // when
            runningMateService.deleteRunningmate(managerUser.getId(), runningMate.getId());
            em.flush();
            em.clear();

            // then
            RunningMate deleted = em.find(RunningMate.class, runningMate.getId());
            assertThat(deleted).isNull();
        }
    }

    @Nested
    @DisplayName("러닝메이트 리스트 테스트")
    class ListTest {

        Course course1;
        Course course2;

        @BeforeEach
        void setUp() {
            course1 = Course.builder()
                    .campus(campus)
                    .name("영등포 스프링")
                    .classNumber("과정1")
                    .instructorName("스프링샘")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(1))
                    .build();

            course2 = Course.builder()
                    .campus(campus)
                    .name("영등포 파이선")
                    .classNumber("과정1")
                    .instructorName("파이선샘")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(1))
                    .build();

            em.persist(course1);
            em.persist(course2);

            for (int i = 1; i <= 12; i++) {
                RunningMate runningMate = RunningMate.builder()
                        .course((i - 1) / 6 < 1 ? course1 : course2)
                        .name("러닝 이름 " + i)
                        .subject("러닝 주제 " + i)
                        .goal("러닝 목표 " + i)
                        .build();

                em.persist(runningMate);
            }

            em.flush();
            em.clear();
        }

        @Test
        @DisplayName("리스트 기본 테스트")
        public void listTest() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchRunningMateRequest request = new SearchRunningMateRequest(null, null);
            PageResponse<SearchRunningMateResponse> response =
                    runningMateService.getRunningmateList(pageRequest, request);

            List<SearchRunningMateResponse> content = response.getContent();

            assertThat(content).hasSize(10);
        }

        @Test
        @DisplayName("과정 검색 테스트")
        public void listSearchCourseTest() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchRunningMateRequest request = new SearchRunningMateRequest(course1.getId(), null);
            PageResponse<SearchRunningMateResponse> response =
                    runningMateService.getRunningmateList(pageRequest, request);

            List<SearchRunningMateResponse> content = response.getContent();

            assertThat(content).hasSize(6);
            assertThat(content.stream().allMatch(r -> r.course().equals("영등포 스프링"))).isTrue();
        }

        @Test
        @DisplayName("이름..? 검색 테스트")
        public void listSearchNameTest() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchRunningMateRequest request = new SearchRunningMateRequest(null, "1");
            PageResponse<SearchRunningMateResponse> response =
                    runningMateService.getRunningmateList(pageRequest, request);

            List<SearchRunningMateResponse> content = response.getContent();

            assertThat(content).hasSize(4);
            assertThat(content.stream().allMatch(r -> r.name().contains("1"))).isTrue();
        }

        @Test
        @DisplayName("정렬 테스트")
        public void listSortTest() {
            PageRequest pageRequest = PageRequest.of(0, 10, Direction.ASC, "name");
            SearchRunningMateRequest request = new SearchRunningMateRequest(null, null);
            PageResponse<SearchRunningMateResponse> response =
                    runningMateService.getRunningmateList(pageRequest, request);

            List<SearchRunningMateResponse> content = response.getContent();

            assertThat(content.stream().map(r -> r.name()).toList())
                    .containsSequence("러닝 이름 1", "러닝 이름 10", "러닝 이름 11", "러닝 이름 12", "러닝 이름 2");
        }
    }

    @Nested
    @DisplayName("러닝메이트 멤버 테스트")
    class MemberCrudTest {

        RunningMate runningMate;
        User leaderUser;
        User memberUser;
        Student leaderStudent;
        Student memberStudent;

        @BeforeEach
        void setUp() {
            runningMate = RunningMate.builder()
                    .course(course)
                    .name("리눅스")
                    .subject("리눅스를 배운다")
                    .goal("리눅스 마스터")
                    .build();

            leaderUser = User.builder()
                    .email("user1@example.com")
                    .role(UserRole.STUDENT)
                    .password("1234")
                    .build();

            memberUser = User.builder()
                    .email("user2@example.com")
                    .role(UserRole.STUDENT)
                    .password("1234")
                    .build();

            leaderStudent = Student.builder()
                    .user(leaderUser)
                    .statusCode(10)
                    .nickname("리더")
                    .name("리더")
                    .firstCourse(course)
                    .gender('M')
                    .birthDate(LocalDate.now())
                    .build();

            memberStudent = Student.builder()
                    .user(memberUser)
                    .statusCode(10)
                    .nickname("멤버")
                    .name("멤버")
                    .firstCourse(course)
                    .gender('F')
                    .birthDate(LocalDate.now())
                    .build();

            em.persist(runningMate);
            em.persist(leaderUser);
            em.persist(memberUser);
            em.persist(leaderStudent);
            em.persist(memberStudent);

            em.flush();
            em.clear();
        }

        @Test
        @DisplayName("멤버 등록")
        public void create() {
            // give
            CreateRunningMateMemberRequest request = new CreateRunningMateMemberRequest(
                    leaderUser.getId(), MemberRole.LEADER, "010-0000-0001");

            // when
            Long createdId = runningMateService.createRunningmateMember(runningMate.getId(),
                    request);

            // then
            RunningMateMember created = em.find(RunningMateMember.class, createdId);
            assertThat(created.getRunningMate().getId()).isEqualTo(runningMate.getId());
            assertThat(created.getRole()).isEqualTo(MemberRole.LEADER);
        }

        @Test
        @DisplayName("유저 아이디와 러닝메이트 아이디가 같은 경우 또는 핸드폰 번호가 같을 경우 이미 등록된 멤버 예외")
        public void createAlreadyRegistered() {
            // give
            CreateRunningMateMemberRequest request = new CreateRunningMateMemberRequest(
                    leaderUser.getId(), MemberRole.LEADER, "010-0000-0001");

            runningMateService.createRunningmateMember(runningMate.getId(), request);

            // when
            BaseException ex = Assertions.assertThrows(BaseException.class,
                    () -> runningMateService.createRunningmateMember(runningMate.getId(), request));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(RunningMateErrorCode.ALREADY_REGISTERED);
        }

        @Test
        @DisplayName("러닝메이트 멤버 조회")
        public void getDetailMember() {
            // give
            RunningMateMember runningMateMember = RunningMateMember.builder()
                    .runningMate(runningMate)
                    .user(memberUser)
                    .role(MemberRole.MEMBER)
                    .phoneNumber("010-0000-0001")
                    .build();

            em.persist(runningMateMember);
            em.flush();
            em.clear();

            // when
            RunningMateMemberDetailResponse response = runningMateService.getRunningmateMember(
                    runningMate.getId(), runningMateMember.getId());

            // then
            assertThat(response.userId()).isEqualTo(runningMateMember.getUser().getId());
        }

        @Test
        @DisplayName("러닝메이트 멤버 수정")
        public void updateMember() {
            // give
            RunningMateMember runningMateMember = RunningMateMember.builder()
                    .runningMate(runningMate)
                    .user(memberUser)
                    .role(MemberRole.MEMBER)
                    .phoneNumber("010-0000-0001")
                    .build();

            em.persist(runningMateMember);
            em.flush();
            em.clear();

            UpdateRunningMateMemberRequest request = new UpdateRunningMateMemberRequest(
                    MemberRole.LEADER, null);

            // when
            Long updatedId = runningMateService.updateRunningmateMember(
                    manager.getId(), runningMate.getId(), runningMateMember.getId(), request);

            // then
            RunningMateMember updated = em.find(RunningMateMember.class, updatedId);
            assertThat(updated.getRole()).isEqualTo(MemberRole.LEADER);
        }

        @Test
        @DisplayName("러닝메이트 멤버 삭제")
        public void deleteMember() {
            // give
            RunningMateMember runningMateMember = RunningMateMember.builder()
                    .runningMate(runningMate)
                    .user(memberUser)
                    .role(MemberRole.MEMBER)
                    .phoneNumber("010-0000-0001")
                    .build();

            em.persist(runningMateMember);
            em.flush();
            em.clear();

            // when
            runningMateService.deleteRunningmateMember(
                    manager.getId(), runningMate.getId(), runningMateMember.getId());

            // then
            RunningMateMember deleted = em.find(RunningMateMember.class, runningMateMember.getId());
            assertThat(deleted).isNull();
        }

        @Test
        @DisplayName("러닝메이트 멤버 목록")
        public void listMember() {
            RunningMate runningMate2 = RunningMate.builder()
                    .course(course)
                    .name("자바를 자바")
                    .subject("자바를 배운다")
                    .goal("자바 마스터")
                    .build();
            em.persist(runningMate2);

            // give
            for (int i = 1; i <= 12; i++) {

                User user = User.builder()
                        .email("member" + i + "@example.com")
                        .role(UserRole.STUDENT)
                        .password("1234")
                        .build();

                Student student = Student.builder()
                        .user(user)
                        .statusCode(10)
                        .nickname((i <= 2 ? "리더" : "멤버") + i)
                        .name((i <= 2 ? "리더" : "멤버") + i)
                        .firstCourse(course)
                        .gender('M')
                        .birthDate(LocalDate.now())
                        .build();

                RunningMateMember runningMateMember = RunningMateMember.builder()
                        .runningMate(i % 2 == 1 ? runningMate : runningMate2)
                        .user(user)
                        .role(i <= 2 ? MemberRole.LEADER : MemberRole.MEMBER)
                        .phoneNumber("010-0000-000" + i)
                        .build();

                em.persist(user);
                em.persist(student);
                em.persist(runningMateMember);
            }

            em.flush();
            em.clear();

            // when
            List<RunningMateMemberListResponse> list = runningMateService.getRunningmateMemberList(
                    runningMate.getId());

            // then
            assertThat(list).hasSize(6);
        }
    }
}