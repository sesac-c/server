package sesac.server.common.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import sesac.server.common.exception.BaseException;
import sesac.server.common.exception.GlobalErrorCode;

/**
 * 엔티티 업데이트 요청의 유효성을 검사하고 준비하는 서비스를 위한 추상 클래스.
 *
 * @param <T> 업데이트 요청 객체의 타입
 * @param <E> 엔티티 객체의 타입
 */
@Log4j2
public abstract class UpdateValidationService<T, E> {

    // @param request 원본 업데이트 요청, @param entity 현재 엔티티 정보,
    // @throws RuntimeException 변경 사항이 없는 경우 발생
    public T validateAndPrepareUpdate(T request, E entity) {
        validateNotAllFieldsNull(request);                  // 모든 필드가 null인지 검사

        Map<String, Boolean> changedFields =
                detectChanges(request, entity);             // 변경된 필드를 감지

        T updatedRequest = createUpdatedRequest(request,
                changedFields);                             // 변경된 필드만 포함하는 새로운 요청 객체를 생성
        validateChanges(changedFields);                     // 변경 사항이 있는지 검증

        logUpdateAsync(entity, updatedRequest, changedFields); // 업데이트 로그를 비동기적으로 기록

        return updatedRequest;                              // 검증된 업데이트 엔티티 반환
    }


    // @param request 업데이트 요청, @throws BaseException 모든 필드가 null인 경우 발생
    protected abstract void validateNotAllFieldsNull(T request);


    // @param request 업데이트 요청, @param entity 현재 엔티티 정보, @return 각 필드의 변경 여부를 나타내는 Map
    protected abstract Map<String, Boolean> detectChanges(T request, E entity);


    // @param newValue 새로운 값, @param oldValue 기존 값, @return 값이 변경되었으면 true, 그렇지 않으면 false
    protected boolean isFieldChanged(String newValue, String oldValue) {
        return newValue != null && !newValue.equals(oldValue);
    }


    // @param changedFields 각 필드의 변경 여부를 나타내는 Map, @throws BaseException 변경된 필드가 없는 경우 발생
    protected void validateChanges(Map<String, Boolean> changedFields) {
        boolean anyFieldChanged = changedFields.values().stream().anyMatch(changed -> changed);
        if (!anyFieldChanged) {
            throw new BaseException(GlobalErrorCode.SAME_AS_PREVIOUS);
        }
    }


    // @param originalRequest 원본 업데이트 요청, @param changedFields 각 필드의 변경 여부를 나타내는 Map
    // @return 변경된 필드만 포함하는 새로운 업데이트 요청 객체
    protected abstract T createUpdatedRequest(T originalRequest,
            Map<String, Boolean> changedFields);


    // @param entity 현재 엔티티 정보, @param updatedRequest 업데이트된 요청 정보,
    // @param changedFields 각 필드의 변경 여부를 나타내는 Map
    protected abstract void logUpdateAsync(E entity, T updatedRequest,
            Map<String, Boolean> changedFields);


    // @param messageSupplier 로그 메시지를 생성하는 Supplier
    protected static void logAsync(Supplier<String> messageSupplier) {
        CompletableFuture.supplyAsync(messageSupplier)
                .thenAccept(log::info)
                .exceptionally(e -> {
                    log.error("로그메시지 빌드 중 에러: ", e);
                    return null;
                });
    }


    // @param entity 현재 엔티티 정보, @param updatedRequest 업데이트된 요청 정보,
    // @param changedFields 각 필드의 변경 여부를 나타내는 Map, @return 생성된 로그 메시지
    protected abstract String buildUpdateLogMessage(E entity, T updatedRequest,
            Map<String, Boolean> changedFields);
}