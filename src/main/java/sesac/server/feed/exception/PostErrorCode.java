package sesac.server.feed.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {
    REQUIRED_TITLE(HttpStatus.BAD_REQUEST, "REQUIRED_TITLE", "제목은 필수입니다."),
    INVALID_TITLE_SIZE(HttpStatus.BAD_REQUEST, "INVALID_TITLE_SIZE", "제목은 1자 이상 20자 이하로 입력해야 합니다."),
    REQUIRED_CONTENT(HttpStatus.BAD_REQUEST, "REQUIRED_CONTENT", "내용은 필수입니다."),
    INVALID_CONTENT_SIZE(HttpStatus.BAD_REQUEST, "INVALID_CONTENT_SIZE",
            "내용은 1자 이상 500자 이하로 입력해야 합니다."),
    NO_POST(HttpStatus.NOT_FOUND, "NO_POST", "게시글이 존재하지 않습니다"),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "NO_PERMISSION", "권한이 없습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "ALREADY_LIKED", "사용자가 이미 해당 게시글에 좋아요를 눌렀습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}