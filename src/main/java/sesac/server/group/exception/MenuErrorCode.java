package sesac.server.group.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum MenuErrorCode implements ErrorCode {

    REQUIRED_NAME(BAD_REQUEST, "메뉴명을 입력해주세요"),
    INVALID_NAME_SIZE(BAD_REQUEST, "메뉴명은 1 ~ 20자로 입력해주세요"),

    REQUIRED_PRICE(BAD_REQUEST, "가격을 입력해주세요"),

    NOT_FOUND_MENU(NOT_FOUND, "메뉴가 없습니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
