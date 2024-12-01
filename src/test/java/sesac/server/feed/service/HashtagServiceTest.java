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
import sesac.server.feed.entity.Hashtag;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostHashtag;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.repository.HashtagRepository;
import sesac.server.feed.repository.PostHashtagRepository;
import sesac.server.user.entity.Student;

@SpringBootTest
@Transactional
@Log4j2
class HashtagServiceTest {

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostHashtagRepository postHashtagRepository;

    @PersistenceContext
    EntityManager em;

    Student student1;

    @BeforeEach
    public void setup() {
        Fixture.em = em;
        Campus campus = Fixture.createCampus("캠퍼스 1");
        Course course = Fixture.createCourse("과정 1", campus);
        student1 = Fixture.createStudent("학생 1", course, 10);
    }


    @Test
    @DisplayName("해시태그 테스트")
    public void hashtagTest2() {
        // when
        hashtagService.saveHashTags(List.of("해시1", "해시2"));
        hashtagService.saveHashTags(List.of("해시1", "해시2", "해시3"));

        em.flush();
        em.clear();

        List<Hashtag> hashtags = hashtagRepository.findAll();

        // than
        assertThat(hashtags).hasSize(3);
    }

    @Test
    @DisplayName("post hashtag 저장 테스트")
    public void hashtagTest3() {
        // give
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student1.getUser())
                .build();

        em.persist(post);

        List<Hashtag> hashtags = hashtagService.saveHashTags(List.of("해시1", "해시2", "해시3"));

        // when
        hashtagService.savePostHashtags(hashtags, post);
        List<PostHashtag> list = postHashtagRepository.findAll();

        // than
        assertThat(list).hasSize(3);
    }
}