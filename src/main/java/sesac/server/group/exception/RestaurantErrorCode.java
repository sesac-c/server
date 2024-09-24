package sesac.server.group.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import sesac.server.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum RestaurantErrorCode implements ErrorCode {

    REQUIRED_NAME(HttpStatus.BAD_REQUEST, "음식점명을 입력해주세요"),
    INVALID_NAME_SIZE(HttpStatus.BAD_REQUEST, "음식점명은 1 ~ 20자로 입력해주세요"),

    REQUIRED_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 입력해주세요"),
    INVALID_CATEGORY_SIZE(HttpStatus.BAD_REQUEST, "카테고리는 1 ~ 20자로 입력해주세요"),

    REQUIRED_ADDRESS(HttpStatus.BAD_REQUEST, "주소를 입력해주세요"),
    INVALID_ADDRESS_SIZE(HttpStatus.BAD_REQUEST, "주소는 1 ~ 150자로 입력해주세요"),

    REQUIRED_ADDRESS_ID(HttpStatus.BAD_REQUEST, "주소 아이디를 입력해주세요"),

    REQUIRED_LATITUDE(HttpStatus.BAD_REQUEST, "위도를 입력해주세요"),
    REQUIRED_LONGITUDE(HttpStatus.BAD_REQUEST, "경도를 입력해주세요");

    private final HttpStatus status;
    private final String message;

    public String getCode() {
        return this.name();
    }
}
