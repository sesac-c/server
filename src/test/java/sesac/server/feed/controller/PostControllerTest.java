package sesac.server.feed.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sesac.server.common.JwtTestUtil;
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.dto.request.UpdatePostRequest;
import sesac.server.feed.entity.PostType;
import sesac.server.feed.exception.PostErrorCode;
import sesac.server.feed.service.PostService;

@Log4j2
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    private PostService postService;

    @Test
    @DisplayName("캠퍼스 등록 요청 성공")
    public void 등록_성공() throws Exception {
        // give
        String token = JwtTestUtil.createTestJwtToken("1", "STUDENT");
        CreatePostRequest request = new CreatePostRequest("제목", "내용", null, null);

        // when & then
        mockMvc.perform(JwtTestUtil.addJwtToken(post("/posts/campus"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        // postService.createPost 메서드를 호출했는지
        verify(postService).createPost(
                any(Long.class),
                any(PostType.class),
                any(CreatePostRequest.class)
        );
    }

    @Test
    @DisplayName("캠퍼스 등록 요청 실패: 제목 + 내용이 null인 경우")
    public void 등록_실패_400() throws Exception {
        // give
        String token = JwtTestUtil.createTestJwtToken("1", "STUDENT");
        CreatePostRequest request = new CreatePostRequest(null, null, null, null);

        // when & then
        mockMvc.perform(JwtTestUtil.addJwtToken(post("/posts/campus"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(PostErrorCode.REQUIRED_TITLE.getMessage()));
    }

    @Test
    @DisplayName("캠퍼스 등록 요청 실패: 토큰이 없는 경우 401 에러")
    public void 등록_실패_401() throws Exception {
        mockMvc.perform(post("/posts/campus"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("캠퍼스 수정 요청 성공")
    public void 수정_성공() throws Exception {
        mockMvc.perform(put("/posts/campus/1"))
                .andExpect(status().isUnauthorized());

        // give
        String token = JwtTestUtil.createTestJwtToken("1", "STUDENT");
        UpdatePostRequest request = new UpdatePostRequest("수정", "수정");

        // when & then
        mockMvc.perform(JwtTestUtil.addJwtToken(put("/posts/campus/1"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("캠퍼스 수정 요청 실패: 제목 길이")
    public void 수정_실패() throws Exception {
        mockMvc.perform(put("/posts/campus/1"))
                .andExpect(status().isUnauthorized());

        // give
        String token = JwtTestUtil.createTestJwtToken("1", "STUDENT");
        UpdatePostRequest request = new UpdatePostRequest("", "수정");

        // when & then
        mockMvc.perform(JwtTestUtil.addJwtToken(put("/posts/campus/1"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(PostErrorCode.INVALID_TITLE_SIZE.getMessage()));
    }
}