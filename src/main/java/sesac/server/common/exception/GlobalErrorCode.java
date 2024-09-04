package sesac.server.common.exception;


import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    NOT_FOUND_PAGE(NOT_FOUND, "페이지를 찾을 수 없습니다."),
    REDIS_CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "Redis 연결 오류가 발생했습니다."),
    REDIS_SAVE_ERROR(INTERNAL_SERVER_ERROR, "Redis에 데이터 저장 중 오류가 발생했습니다."),
    REDIS_RETRIEVE_ERROR(INTERNAL_SERVER_ERROR, "Redis에서 데이터 조회 중 오류가 발생했습니다."),
    REDIS_DELETE_ERROR(INTERNAL_SERVER_ERROR, "Redis에서 데이터 삭제 중 오류가 발생했습니다."),
    REDIS_KEY_NOT_FOUND_ERROR(NOT_FOUND, "요청한 키를 찾을 수 없습니다."),
    REDIS_EXPIRATION_ERROR(INTERNAL_SERVER_ERROR, "edis 키 만료 설정 중 오류가 발생했습니다."),
    REDIS_KEY_CHECK_ERROR(INTERNAL_SERVER_ERROR,
            "Redis 키 존재 여부 확인 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
