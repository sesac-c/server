package sesac.server.feed.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ReplyErrorCode implements ErrorCode {
    REQUIRED_CONTENT(BAD_REQUEST, "내용은 필수입니다."),
    INVALID_CONTENT_SIZE(BAD_REQUEST,
            "내용은 1자 이상 100자 이하로 입력해야 합니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}