package sesac.server.feed.service;

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
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.common.Fixture;
import sesac.server.feed.dto.request.CreateNoticeRequest;
import sesac.server.feed.dto.response.NoticeResponse;
import sesac.server.feed.entity.Notice;
import sesac.server.feed.entity.NoticeType;
import sesac.server.user.entity.Manager;
import sesac.server.user.entity.Student;

@Log4j2
@SpringBootTest
@Transactional
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @PersistenceContext
    EntityManager em;

    Student student;
    Manager manager;

    Campus campus;
    Course course;

    @BeforeEach
    public void setUp() {
        Fixture.em = em;
        campus = Fixture.createCampus("영등포");
        course = Fixture.createCourse("영등포 자바", campus);

        student = Fixture.createStudent("student1", course, 10);
        manager = Fixture.createManager("manager1", campus);


    }

    @Test
    @DisplayName("등록 테스트")
    public void create() {
        // give
        CreateNoticeRequest request =
                new CreateNoticeRequest("제목", "내용", "", null, null, course.getId());

        // when
        Notice created = noticeService.createNotice(manager.getId(), request, NoticeType.ALL);

        // then
        Notice notice = em.find(Notice.class, created.getId());
        assertThat(notice.getTitle()).isEqualTo("제목");
        assertThat(notice.getContent()).isEqualTo("내용");
        assertThat(notice.getType()).isEqualTo(NoticeType.ALL);
    }

    @Test
    @DisplayName("게시글 상세")
    public void postDetailTest() {
        // give
        Notice created = Notice.builder()
                .title("제목")
                .content("내용")
                .type(NoticeType.ALL)
                .user(manager.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        // when
        NoticeResponse notice = noticeService.getNotice(manager.getUser().getId(), created.getId());

        // then
        assertThat(notice.title()).isEqualTo("제목");
        assertThat(notice.content()).isEqualTo("내용");
        assertThat(notice.campusName()).isEqualTo(manager.getCampus().getName());
    }

    // TODO 테스트 코드 수정
    @Test
    @DisplayName("주요 공지")
    public void importantNotice() {
        // give
        Notice created = Notice.builder()
                .title("제목 1")
                .content("내용 1")
                .type(NoticeType.ALL)
                .user(manager.getUser())
                .importance(1)
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        noticeService.getImportantNotices();
    }

    @Test
    @DisplayName("중복 해시코드")
    public void hashcodeTest() {
        // give
        CreateNoticeRequest request1 = new CreateNoticeRequest("제목", "내용", null,
                List.of("해시1", "해시2"), 1, course.getId());
        CreateNoticeRequest request2 = new CreateNoticeRequest("제목", "내용", null,
                List.of("해시1", "해시2", "해시3"),
                1, course.getId());

        // when
        Notice created1 = noticeService.createNotice(manager.getId(), request1, NoticeType.ALL);
        Notice created2 = noticeService.createNotice(manager.getId(), request2, NoticeType.ALL);

        em.flush();
        em.clear();

        // then
        Notice notice1 = em.find(Notice.class, created1.getId());
        Notice notice2 = em.find(Notice.class, created2.getId());

        assertThat(notice1.getHashtags()).hasSize(2);
        assertThat(notice2.getHashtags()).hasSize(3);
    }
}