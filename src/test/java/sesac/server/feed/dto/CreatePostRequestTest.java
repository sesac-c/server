package sesac.server.feed.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import sesac.server.common.DtoTest;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.ErrorCode;
import sesac.server.feed.dto.request.CreatePostRequest;
import sesac.server.feed.exception.PostErrorCode;

@Log4j2
class CreatePostRequestTest extends DtoTest {

    List<ErrorCode> errorsCodes = List.of(
            PostErrorCode.REQUIRED_TITLE,
            PostErrorCode.INVALID_TITLE_SIZE,
            PostErrorCode.REQUIRED_CONTENT,
            PostErrorCode.INVALID_CONTENT_SIZE
    );

    @Test
    @DisplayName("제목이 빈 값인 경우")
    public void titleIsEmpty() {
        CreatePostRequest request = new CreatePostRequest("", null, null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.REQUIRED_TITLE);
    }

    @Test
    @DisplayName("제목이 null인 경우")
    public void titleIsNull() {
        CreatePostRequest request = new CreatePostRequest("", null, null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.REQUIRED_TITLE);
    }

    @Test
    @DisplayName("제목이 20자 이상인 경우")
    public void titleSizeInvalid() {
        String title = testText(21);
        log.info("제목 길이: {} ", title.length());

        CreatePostRequest request = new CreatePostRequest(title,
                null, null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.INVALID_TITLE_SIZE);
    }

    @Test
    @DisplayName("내용이 빈 값인 경우")
    public void contentIsEmpty() {
        CreatePostRequest request = new CreatePostRequest("제목", "", null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.REQUIRED_CONTENT);
    }

    @Test
    @DisplayName("내용이 null인 경우")
    public void contentIsNull() {
        CreatePostRequest request = new CreatePostRequest("제목", null, null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.REQUIRED_CONTENT);
    }

    @Test
    @DisplayName("제목이 500자 이상인 경우")
    public void contentSizeInvalid() {
        String content = testText(501);
        log.info("내용 길이: {} ", content.length());

        CreatePostRequest request = new CreatePostRequest("제목",
                content, null, null);
        BindingResult bindingResult = getBindingResult(request);

        BaseException ex = assertThrows(BaseException.class,
                () -> bindingResultHandler.handleBindingResult(bindingResult, errorsCodes));

        assertThat(ex.getErrorCode()).isEqualTo(PostErrorCode.INVALID_CONTENT_SIZE);
    }

    @Test
    @DisplayName("통과")
    public void complete() {
        CreatePostRequest request = new CreatePostRequest("제목",
                "내용", null, null);
        BindingResult bindingResult = getBindingResult(request);

        assertThat(bindingResult.hasErrors()).isFalse();
    }

}