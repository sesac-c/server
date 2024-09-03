package sesac.server.feed.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
    REQUIRED_TITLE(BAD_REQUEST, "제목은 필수입니다."),
    INVALID_TITLE_SIZE(BAD_REQUEST, "제목은 1자 이상 20자 이하로 입력해야 합니다."),
    REQUIRED_CONTENT(BAD_REQUEST, "내용은 필수입니다."),
    INVALID_CONTENT_SIZE(BAD_REQUEST, "내용은 1자 이상 500자 이하로 입력해야 합니다."),
    REQUIRED_NOTICE_TYPE(BAD_REQUEST, "공지 타입을 선택해 주세요"),
    NO_POST(NOT_FOUND, "게시글이 존재하지 않습니다"),
    NO_PERMISSION(FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}