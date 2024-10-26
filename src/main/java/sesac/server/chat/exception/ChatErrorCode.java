package sesac.server.chat.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_ALREADY_EXISTS(BAD_REQUEST, "이미 채팅방이 존재합니다."),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    NOT_COURSE_MEMBER(FORBIDDEN, "해당 과정의 구성원이 아닙니다."),
    CHAT_ROOM_INACTIVE(BAD_REQUEST, "비활성화된 채팅방입니다."),

    REQUIRED_CONTENT(BAD_REQUEST, "채팅 내용은 필수입니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}