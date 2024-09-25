package sesac.server.group.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RestaurantErrorCode implements ErrorCode {

    REQUIRED_NAME(BAD_REQUEST, "음식점명을 입력해주세요"),
    INVALID_NAME_SIZE(BAD_REQUEST, "음식점명은 1 ~ 20자로 입력해주세요"),

    REQUIRED_CATEGORY(BAD_REQUEST, "카테고리를 입력해주세요"),
    INVALID_CATEGORY_SIZE(BAD_REQUEST, "카테고리는 1 ~ 20자로 입력해주세요"),

    REQUIRED_ADDRESS(BAD_REQUEST, "주소를 입력해주세요"),
    INVALID_ADDRESS_SIZE(BAD_REQUEST, "주소는 1 ~ 150자로 입력해주세요"),

    REQUIRED_ADDRESS_ID(BAD_REQUEST, "주소 아이디를 입력해주세요"),

    REQUIRED_LATITUDE(BAD_REQUEST, "위도를 입력해주세요"),
    REQUIRED_LONGITUDE(BAD_REQUEST, "경도를 입력해주세요"),

    NOT_FOUND_RESTAURANT(NOT_FOUND, "없는 식당입니다."),

    INCOMPLETE_ADDRESS_INFO(BAD_REQUEST, "주소, 주소 아이디, 경도, 위도에 대한 모든 입력이 필요합니다.");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
