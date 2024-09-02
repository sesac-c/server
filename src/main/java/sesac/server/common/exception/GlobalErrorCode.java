package sesac.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
            "서버에서 오류가 발생했습니다."),
    REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_CONNECTION_ERROR",
            "Redis 연결 오류가 발생했습니다."),
    REDIS_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_SAVE_ERROR",
            "Redis에 데이터 저장 중 오류가 발생했습니다."),
    REDIS_RETRIEVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_RETRIEVE_ERROR",
            "Redis에서 데이터 조회 중 오류가 발생했습니다."),
    REDIS_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_DELETE_ERROR",
            "Redis에서 데이터 삭제 중 오류가 발생했습니다."),
    REDIS_KEY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "REDIS_KEY_NOT_FOUND_ERROR",
            "요청한 키를 찾을 수 없습니다."),
    REDIS_EXPIRATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_EXPIRATION_ERROR",
            "edis 키 만료 설정 중 오류가 발생했습니다."),
    REDIS_KEY_CHECK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_KEY_CHECK_ERROR",
            "Redis 키 존재 여부 확인 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
