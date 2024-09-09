package sesac.server.campus.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ErrorCode {

    REQUIRED_NAME(BAD_REQUEST, "강의 이름은 필수입니다."),
    INVALID_NAME_SIZE(BAD_REQUEST, "강의 이름은 1자 이상 50자 이하로 입력해야 합니다."),

    REQUIRED_CLASS_NUMBER(BAD_REQUEST, "강의 기수 제공은 필수입니다."),
    INVALID_CLASS_NUMBER_SIZE(BAD_REQUEST,
            "강의 기수 1자 이상 100자 이하로 입력해야 합니다."),

    REQUIRED_INSTRUCTOR_NAME(BAD_REQUEST, "강사 이름이 입력되지 않았습니다"),
    INVALID_INSTRUCTOR_NAME_PATTERN(BAD_REQUEST, "강사 이름은 한글만 입력 가능합니다"),
    INVALID_INSTRUCTOR_NAME_SIZE(BAD_REQUEST, "강사 이름은 1 ~ 5자로 입력해주세요"),

    REQUIRED_START_DATE(BAD_REQUEST, "개강일 입력은 필수입니다."),
    REQUIRED_END_DATE(BAD_REQUEST, "종강일 입력은 필수입니다."),

    NO_COURSE(NOT_FOUND, "강의가 없습니다."),
    NO_PERMISSION(FORBIDDEN, "해당 강의의 수정 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}