package sesac.server.group.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RunningMateErrorCode implements ErrorCode {
    REQUIRED_NAME(HttpStatus.BAD_REQUEST, "러닝메이트 이름을 입력해주세요"),
    REQUIRED_SUBJECT(HttpStatus.BAD_REQUEST, "러닝메이트 주제를 입력해주세요"),
    REQUIRED_GOAL(HttpStatus.BAD_REQUEST, "러닝메이트 목표를 입력해주세요"),
    REQUIRED_COURSE(HttpStatus.BAD_REQUEST, "러닝메이트 과정을 선택해주세요"),
    NO_RUNNING_MATE(HttpStatus.BAD_REQUEST, "러닝메이트가 없습니다."),
    REQUIRED_USER(HttpStatus.BAD_REQUEST, "유저를 입력해주세요"),
    REQUIRED_ROLE(HttpStatus.BAD_REQUEST, "러닝메이트를 입력해주세요"),
    REQUIRED_PHONE(HttpStatus.BAD_REQUEST, "핸드폰 번호를 입력해주세요"),
    INVALID_PHONE(HttpStatus.BAD_REQUEST, "핸드폰 번호를 정확하게 입력해주세요"),
    ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 멤버입니다."),
    NO_RUNNING_MATE_MEMBER(HttpStatus.BAD_REQUEST, "러닝메이트 멤버가 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
