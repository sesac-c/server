package sesac.server.account.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATED_EMAIL", "이미 가입된 이메일 입니다"),
    REQUIRED_EMAIL(HttpStatus.BAD_REQUEST, "REQUIRED_EMAIL", "이메일 주소를 입력해 주세요"),
    INVALID_EMAIL_PATTERN(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_PATTERN", "이메일 형식이 아닙니다"),

    REQUIRED_PASSWORD(HttpStatus.BAD_REQUEST, "REQUIRED_PASSWORD", "비밀번호를 입력해 주세요"),
    INVALID_PASSWORD_PATTERN(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_PATTERN",
            "비밀번호는 8~20자 이내로 영어, 숫자, 특수문자를 포함해야 합니다"),
    REQUIRED_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, "REQUIRED_PASSWORD_CONFIRM",
            "비밀번호 확인이 입력되지 않았습니다"),
    DIFFERENT_PASSWORD_CONFIRM(HttpStatus.BAD_REQUEST, "DIFFERENT_PASSWORD_CONFIRM",
            "비밀번호 확인이 일치하지 않습니다"),

    REQUIRED_NAME(HttpStatus.BAD_REQUEST, "REQUIRED_NAME", "이름이 입력되지 않았습니다"),
    INVALID_NAME_PATTERN(HttpStatus.BAD_REQUEST, "INVALID_NAME_PATTERN", "이름은 한글만 입력 가능합니다"),
    INVALID_NAME_SIZE(HttpStatus.BAD_REQUEST, "INVALID_NAME_SIZE", "이름은 1 ~ 5자로 입력해주세요"),

    REQUIRED_BIRTH(HttpStatus.BAD_REQUEST, "REQUIRED_BIRTH", "주민번호가 입력되지 않았습니다"),
    INVALID_BIRTH_PATTERN(HttpStatus.BAD_REQUEST, "INVALID_BIRTH_PATTERN", "주민번호는 숫자만 입력 가능합니다"),
    INVALID_BIRTH_SIZE(HttpStatus.BAD_REQUEST, "INVALID_BIRTH_SIZE", "주민번호 앞자리를 올바르게 입력해주세요"),

    REQUIRED_GENDER(HttpStatus.BAD_REQUEST, "REQUIRED_GENDER", "주민번호가 입력되지 않았습니다"),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, "INVALID_GENDER", "주민번호 뒷자리는 1 ~ 4의 값을 입력해주세요"),

    REQUIRED_COURSE(HttpStatus.BAD_REQUEST, "REQUIRED_COURSE", "과정을 선택해주세요"),
    NO_COURSE(HttpStatus.BAD_REQUEST, "NO_COURSE", "과정이 없습니다"),

    NO_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "NO_EMAIL_OR_PASSWORD",
            "아이디 또는 비밀번호가 잘못되었습니다. 다시 입력해 주세요"),

    PENDING_ACCOUNT(HttpStatus.UNAUTHORIZED, "PENDING_ACCOUNT", "아직 승인되지 않은 회원입니다."),
    //    APPROVED_ACCOUNT(HttpStatus.BAD_REQUEST, "", ""),
    HOLD_ACCOUNT(HttpStatus.UNAUTHORIZED, "HOLD_ACCOUNT", "승인 보류된 회원입니다."),
    REJECTED_ACCOUNT(HttpStatus.UNAUTHORIZED, "REJECTED_ACCOUNT", "승인 거절된 회원입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND", "해당 이메일로 등록된 계정을 찾을 수 없습니다."),
    REQUIRED_UUID(HttpStatus.BAD_REQUEST, "REQUIRED_UUID", "UUID가 필요합니다."),
    PASSWORD_SAME_AS_PREVIOUS(HttpStatus.BAD_REQUEST, "PASSWORD_SAME_AS_PREVIOUS",
            "새 비밀번호가 이전 비밀번호와 동일합니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
