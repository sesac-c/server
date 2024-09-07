package sesac.server.campus.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CampusErrorCode implements ErrorCode {

    REQUIRED_NAME(BAD_REQUEST, "캠퍼스 이름은 필수입니다."),
    INVALID_NAME_SIZE(BAD_REQUEST, "캠퍼스 이름은 1자 이상 50자 이하로 입력해야 합니다."),
    REQUIRED_ADDRESS(BAD_REQUEST, "캠퍼스 주소는 필수입니다."),
    INVALID_ADDRESS_SIZE(BAD_REQUEST,
            "캠퍼스 주소는 1자 이상 100자 이하로 입력해야 합니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}