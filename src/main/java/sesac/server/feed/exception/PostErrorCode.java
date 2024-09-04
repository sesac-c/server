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

    REQUIRED_TITLE(HttpStatus.BAD_REQUEST, "제목은 필수입니다."),
    INVALID_TITLE_SIZE(HttpStatus.BAD_REQUEST, "제목은 1자 이상 20자 이하로 입력해야 합니다."),
    REQUIRED_CONTENT(HttpStatus.BAD_REQUEST, "내용은 필수입니다."),
    INVALID_CONTENT_SIZE(HttpStatus.BAD_REQUEST,
            "내용은 1자 이상 500자 이하로 입력해야 합니다."),
    NO_POST(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다"),
    NO_NOTICE(HttpStatus.NOT_FOUND, "공지가 존재하지 않습니다"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "사용자가 이미 해당 게시글에 좋아요를 눌렀습니다."),
    NOT_LIKED(HttpStatus.BAD_REQUEST, "사용자가 아직 해당 게시글에 좋아요를 누르지 않았습니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}