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
public enum ReplyErrorCode implements ErrorCode {
    REQUIRED_CONTENT(BAD_REQUEST, "내용은 필수입니다."),
    INVALID_CONTENT_SIZE(BAD_REQUEST, "내용은 1자 이상 100자 이하로 입력해야 합니다."),
    NO_REPLY(NOT_FOUND, "댓글이 존재하지 않습니다."),
    CONTENT_SAME_AS_PREVIOUS(BAD_REQUEST, "새 댓글내용이 이전 댓글내용과 동일합니다."),

    NO_PERMISSION(FORBIDDEN, "댓글 수정 권한이 없습니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}