package sesac.server.feed.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.campus.entity.Campus;
import sesac.server.campus.entity.Course;
import sesac.server.common.Fixture;
import sesac.server.common.dto.PageResponse;
import sesac.server.common.exception.BaseException;
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.dto.request.PostListRequest;
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.feed.dto.response.ExtendedPostListResponse;
import sesac.server.feed.dto.response.PostListResponse;
import sesac.server.feed.dto.response.PostResponse;
import sesac.server.feed.entity.Post;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.user.entity.Student;

@SpringBootTest
@Transactional
@Log4j2
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private HashtagService hashtagService;

    @PersistenceContext
    EntityManager em;

    Student student1;
    Student student2;

    @BeforeEach
    public void setup() {
        Fixture.em = em;
        Campus campus = Fixture.createCampus("캠퍼스 1");
        Course course = Fixture.createCourse("과정 1", campus);
        student1 = Fixture.createStudent("학생 1", course, 10);
        student2 = Fixture.createStudent("학생 2", course, 10);
    }


    @Test
    @DisplayName("게시글 작성")
    public void createPostTest() {
        // give
        CreatePostRequest request = new CreatePostRequest("제목", "내용", List.of("해시1", "해시2"), null,
                null);

        // when
        Post created = postService.createPost(student1.getId(), PostType.CAMPUS, request);
        em.flush();
        em.clear();

        // then
        Post post = em.find(Post.class, created.getId());
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
        assertThat(post.getHashtags()).hasSize(2);
        assertThat(post.getType()).isEqualTo(PostType.CAMPUS);
    }

    @Test
    @DisplayName("중복 해시태그")
    public void hashtagTest() {
        // give
        CreatePostRequest request1 = new CreatePostRequest("제목", "내용", List.of("해시1", "해시2"), null,
                null);
        CreatePostRequest request2 = new CreatePostRequest("제목", "내용", List.of("해시1", "해시2", "해시3"),
                null, null);

        // when
        Post created1 = postService.createPost(student1.getId(), PostType.CAMPUS, request1);
        Post created2 = postService.createPost(student1.getId(), PostType.CAMPUS, request2);

        em.flush();
        em.clear();

        // then
        Post post1 = em.find(Post.class, created1.getId());
        Post post2 = em.find(Post.class, created2.getId());

        assertThat(post1.getHashtags()).hasSize(2);
        assertThat(post2.getHashtags()).hasSize(3);
    }

    @Test
    @DisplayName("게시글 상세")
    public void postDetailTest() {
        // give
        Post created = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student1.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        // when
        PostResponse post = postService.getPostDetail(student1.getId(), created.getId());

        // then
        assertThat(post.title()).isEqualTo("제목");
        assertThat(post.content()).isEqualTo("내용");
        assertThat(post.nickname()).isEqualTo(student1.getNickname());
    }

    @Test
    @DisplayName("게시글 수정")
    public void postUpdateTest() {
        // give
        Post created = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student1.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        CustomPrincipal principal = new CustomPrincipal(student1.getId(), "STUDENT");
        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용");

        // when
        postService.updatePost(principal, created.getId(), request);

        // then
        Post post = em.find(Post.class, created.getId());
        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getContent()).isEqualTo("수정 내용");
    }

    @Test
    @DisplayName("게시글 수정 권한 없음")
    public void postUpdateNoPermissionTest() {
        // give
        Post created = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student2.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        CustomPrincipal principal = new CustomPrincipal(student1.getId(), "STUDENT");
        UpdatePostRequest request = new UpdatePostRequest("수정 제목", "수정 내용");

        // when..?
        BaseException ex = assertThrows(BaseException.class,
                () -> postService.updatePost(principal, created.getId(), request));

        // then..?
        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.NO_PERMISSION);
    }

    @Test
    @DisplayName("게시글 삭제")
    public void postDeleteTest() {
        // give
        Post created = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student1.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        CustomPrincipal principal = new CustomPrincipal(student1.getId(), "STUDENT");

        // when
        postService.deletePost(principal, created.getId());

        // then
        BaseException ex = assertThrows(BaseException.class,
                () -> postService.deletePost(principal, created.getId()));
        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.NO_POST);
    }

    @Test
    @DisplayName("게시글 삭제 권한 없음")
    public void postDeleteNoPermissionTest() {
        // give
        Post created = Post.builder()
                .title("제목")
                .content("내용")
                .type(PostType.CAMPUS)
                .user(student2.getUser())
                .build();

        em.persist(created);
        em.flush();
        em.clear();

        CustomPrincipal principal = new CustomPrincipal(student1.getId(), "STUDENT");

        // when..?
        BaseException ex = assertThrows(BaseException.class,
                () -> postService.deletePost(principal, created.getId()));

        // then..?
        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.NO_PERMISSION);
    }

    @Nested
    class ListTest {

        @BeforeEach
        public void setup() {
            for (int i = 1; i <= 12; i++) {
                Student student = i % 2 == 0 ? student1 : student2;
                Post post = Post.builder()
                        .title("제목_" + i)
                        .content("내용_" + i)
                        .type(i % 2 == 0 ? PostType.CAMPUS : PostType.ALL)
                        .user(student.getUser())
                        .build();

                em.persist(post);
            }

            em.flush();
            em.clear();
        }

        @Test
        @DisplayName("게시글 목록")
        public void postListTest() {

            // give
            Pageable pageable1 = PageRequest.of(0, 10);
            PostListRequest request = new PostListRequest(null, null);

            // when
            PageResponse<PostListResponse> list = postService.getPostList(pageable1, request, null);

            // then
            assertThat(list.getContent()).hasSize(10);
        }


        @Test
        @DisplayName("게시글 목록 매니저")
        public void getExtendedPostList() {

            // give
            Pageable pageable1 = PageRequest.of(0, 10);
            PostListRequest request = new PostListRequest(null, null);

            // when
            PageResponse<ExtendedPostListResponse> response = postService.getExtendedPostList(
                    pageable1,
                    request, null);

            // then
            assertThat(response.getContent()).hasSize(10);
        }

        @Test
        @DisplayName("게시글 목록 매니저 타입 검색")
        public void getExtendedPostListSearchType() {

            // give
            Pageable pageable1 = PageRequest.of(0, 10);
            PostListRequest request = new PostListRequest(null, null);

            // when
            PageResponse<ExtendedPostListResponse> response = postService.getExtendedPostList(
                    pageable1, request, PostType.CAMPUS);

            // then
            assertThat(response.getContent()).hasSize(6);
        }
    }
}