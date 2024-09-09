package sesac.server.campus.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sesac.server.campus.dto.response.CourseResponse;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;

@Log4j2
@SpringBootTest
@Transactional
class CourseServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private CourseService courseService;

    private Long campus1Id;
    private Long campus2Id;

    @BeforeEach
    public void before() {
        Campus campus1 = Campus.builder().name("영등포 캠퍼스").address("영등포").build();
        Campus campus2 = Campus.builder().name("금천 캠퍼스").address("금천").build();

        em.persist(campus1);
        em.persist(campus2);
        campus1Id = campus1.getId();
        campus2Id = campus2.getId();
        LocalDate now = LocalDate.now();
        em.persist(Course.builder().classNumber("1").name("영등포 자바").instructorName("김선생")
                .startDate(now).endDate(now.plusMonths(1))
                .campus(campus1).build());
        em.persist(Course.builder().classNumber("2").name("영등포 파이썬").instructorName("이선생")
                .startDate(now).endDate(now.plusMonths(1))
                .campus(campus1).build());
        em.persist(Course.builder().classNumber("3").name("금천 자바").instructorName("박선생")
                .startDate(now).endDate(now.plusMonths(1))
                .campus(campus2).build());
        em.persist(Course.builder().classNumber("4").name("금천 파이썬").instructorName("최선생")
                .startDate(now).endDate(now.plusMonths(1))
                .campus(campus2).build());

    }

    @Test
    @DisplayName("전체 조회 테스트")
    public void findAll() {
        List<CourseResponse> list = courseService.findAll(campus1Id);

        list.forEach(log::info);
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(2);
    }
}