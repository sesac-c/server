package sesac.server.campus.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sesac.server.campus.dto.response.CampusResponse;
import sesac.server.campus.entity.Campus;

@Log4j2
@SpringBootTest
@Transactional
class CampusServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private CampusService campusService;

    @BeforeEach
    public void before() {
        em.persist(Campus.builder().name("영등포 캠퍼스").address("영등포").build());
        em.persist(Campus.builder().name("금천 캠퍼스").address("금천").build());
        em.persist(Campus.builder().name("동대문 캠퍼스").address("동대문").build());
    }

    @Test
    @DisplayName("전체 조회 테스트")
    public void findAll() {
        List<CampusResponse> list = campusService.findAll();

        list.forEach(log::info);
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(3);
    }
}