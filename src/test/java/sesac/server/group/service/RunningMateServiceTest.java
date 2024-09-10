package sesac.server.group.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.log4j.Log4j2;
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
import sesac.server.group.dto.request.CreateRunningMateRequest;
import sesac.server.group.dto.request.SearchRunningMateRequest;
import sesac.server.group.dto.request.UpdateRunningMateRequest;
import sesac.server.group.dto.response.RunningMateDetailResponse;
import sesac.server.group.dto.response.SearchRunningMateResponse;
import sesac.server.group.entity.RunningMate;

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

        em.persist(campus);
        em.persist(course);
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
            Long updatedId = runningMateService.updateRunningmate(
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
            runningMateService.deleteRunningmate(runningMate.getId());
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

            List<SearchRunningMateResponse> content = response.content();

            assertThat(content).hasSize(10);
        }

        @Test
        @DisplayName("과정 검색 테스트")
        public void listSearchCourseTest() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            SearchRunningMateRequest request = new SearchRunningMateRequest(course1.getId(), null);
            PageResponse<SearchRunningMateResponse> response =
                    runningMateService.getRunningmateList(pageRequest, request);

            List<SearchRunningMateResponse> content = response.content();

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

            List<SearchRunningMateResponse> content = response.content();

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

            List<SearchRunningMateResponse> content = response.content();

            assertThat(content.stream().map(r -> r.name()).toList())
                    .containsSequence("러닝 이름 1", "러닝 이름 10", "러닝 이름 11", "러닝 이름 12", "러닝 이름 2");
        }
    }
}