package sesac.server.common.util;

import io.lettuce.core.RedisException;
import java.time.Duration;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;

@Component
@Log4j2
public class RedisUtil<T> {

    private RedisTemplate<String, T> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, T value) throws Exception {
        setValue(key, value, null);
    }

    public void setValue(String key, T value, Duration duration) throws Exception {
        try {
            if (duration == null) {
                redisTemplate.opsForValue().set(key, value);
            } else {
                redisTemplate.opsForValue().set(key, value, duration);
            }
        } catch (RedisException e) {
            log.error("[setValue] Redis 저장에 실패함-key: {}, value: {}", key, value, e);
            throw new BaseException(GlobalErrorCode.REDIS_SAVE_ERROR);
        }
    }

    public T getValue(String key) throws Exception {
        try {
            T value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.error("[getValue] Redis 해당 키로 저장된 value가 없음-key: {}", key);
                throw new BaseException(GlobalErrorCode.REDIS_KEY_NOT_FOUND_ERROR);
            }
            return value;
        } catch (RedisException e) {
            log.error("[getValue] Redis 내부적 오류로 값을 가져오지 못함-key: {}", key, e);
            throw new BaseException(GlobalErrorCode.REDIS_RETRIEVE_ERROR);
        }
    }

    public void deleteValue(String key) throws Exception {
        try {
            Boolean result = redisTemplate.delete(key);
            if (Boolean.FALSE.equals(result)) {
                log.error("[deleteValue] Redis 해당 키로 저장된 value가 없음-key: {}", key);
                throw new BaseException(GlobalErrorCode.REDIS_KEY_NOT_FOUND_ERROR);
            }
        } catch (RedisException e) {
            log.error("[deleteValue] Redis 내부적 오류로 값을 삭제하지 못함-key: {}", key, e);
            throw new BaseException(GlobalErrorCode.REDIS_DELETE_ERROR);
        }
    }

    public boolean hasKey(String key) throws Exception {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (RedisException e) {
            log.error("[hasKey] Redis 내부적 오류로 키 존재 여부를 확인하지 못함-key: {}", key, e);
            throw new BaseException(GlobalErrorCode.REDIS_KEY_CHECK_ERROR);
        }
    }

    public boolean isValueEqual(String key, T compareValue) throws Exception {
        try {
            T savedValue = getValue(key);
            return Objects.equals(compareValue, savedValue); // 일치 여부 boolean 값 반환
        } catch (BaseException e) {
            if (e.getErrorCode() == GlobalErrorCode.REDIS_KEY_NOT_FOUND_ERROR) {
                return false;                                // 키가 없는 경우 false 반환
            }
            throw e;                                        // 다른 예외의 경우 그대로 던짐
        }
    }
}