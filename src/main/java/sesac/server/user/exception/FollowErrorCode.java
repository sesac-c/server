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
public enum FollowErrorCode implements ErrorCode {
    SELF_FOLLOW_NOT_ALLOWED(BAD_REQUEST, "자기 자신을 팔로우할 수 없습니다."),
    ALREADY_FOLLOWING(CONFLICT, "이미 팔로우하고 있는 사용자입니다."),

    SELF_UNFOLLOW_NOT_ALLOWED(BAD_REQUEST, "자기 자신을 언팔로우할 수 없습니다."),
    SELF_REMOVE_FOLLOWING_NOT_ALLOWED(BAD_REQUEST, "자신을 팔로잉 목록에서 제거할 수 없습니다."),
    NOT_FOLLOWING(NOT_FOUND, "팔로우 관계가 존재하지 않습니다.");


    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}