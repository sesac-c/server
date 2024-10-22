package sesac.server.chat.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    CHAT_ROOM_ALREADY_EXISTS(BAD_REQUEST, "이미 채팅방이 존재합니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}