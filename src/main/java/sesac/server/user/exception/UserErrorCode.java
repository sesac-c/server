package sesac.server.user.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NO_USER(NOT_FOUND, "회원이 존재하지 않습니다"),
    NO_MANAGER(NOT_FOUND, "매니저가 존재하지 않습니다"),
    REQUIRED_REJECT_REASON(BAD_REQUEST, "승인 거절 이유를 입력해주세요"),
    NO_RECEIVER(NOT_FOUND, "받는 사람이 존재하지 않습니다."),
    REQUIRED_MESSAGE(BAD_REQUEST, "메시지 내용은 필수입니다."),
    NO_MESSAGE(NOT_FOUND, "메시지가 존재하지 않습니다."),
    INVALID_USER_ROLE(NOT_FOUND, "유효하지 않은 사용자 역할입니다."),

    REQUIRED_NICKNAME(BAD_REQUEST, "닉네임은 필수입니다."),
    INVALID_NICKNAME_SIZE(BAD_REQUEST, "닉네임은 1 ~ 10자로 입력해주세요"),
    INVALID_NICKNAME(BAD_REQUEST, "한글, 숫자, 띄어쓰기만 사용 가능합니다."),
    INVALID_NICKNAME_COMBINATION(BAD_REQUEST, "닉네임을 숫자만으로 구성할 수 없습니다."),

    NO_COURSE_CHANGE_REQUEST(NOT_FOUND, "과정 변경 신청이 존재하지 않습니다"),
    EXISTING_COURSE_CHANGE_REQUEST(CONFLICT, "이미 처리 중인 강의 변경 신청이 있습니다"),
    SAME_COURSE_CHANGE_REQUEST(BAD_REQUEST, "현재 수강 중인 강의와 동일한 강의로 변경 신청할 수 없습니다"),
    NO_COURSE_CHANGE_REQUEST_STATUS(BAD_REQUEST, "과정 변경 상태가 입력되지 않았습니다."),
    INVALID_STATUS_CODE(BAD_REQUEST, "올바른 과정 변경 상태가 아닙니다."),
    NO_COURSE_CHANGE_REQUEST_REJECT_REASON(BAD_REQUEST, "보류 또는 거절 사유가 입력되지 않았습니다."),


    PASSWORD_SAME_AS_PREVIOUS(BAD_REQUEST,
            "새 비밀번호가 이전 비밀번호와 동일합니다."),
    REQUIRED_PASSWORD(BAD_REQUEST, "비밀번호를 입력해 주세요"),
    INVALID_PASSWORD_PATTERN(BAD_REQUEST, "비밀번호는 8~20자 이내로 영어, 숫자, 특수문자를 포함해야 합니다"),
    REQUIRED_PASSWORD_CONFIRM(BAD_REQUEST, "비밀번호 확인이 입력되지 않았습니다"),
    DIFFERENT_PASSWORD_CONFIRM(BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}