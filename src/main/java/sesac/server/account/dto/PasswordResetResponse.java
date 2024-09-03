package sesac.server.account.dto;

import java.util.HashMap;
import java.util.Map;

public record PasswordResetResponse(
        boolean success,
        String message,
        Map<String, String> data
) {

    public PasswordResetResponse {
        data = new HashMap<>(data);
    }

    public void addData(String key, String value) {
        this.data.put(key, value);
    }

    public static PasswordResetResponse emailVerificationSuccess(String code) {
        PasswordResetResponse response = new PasswordResetResponse(
                true,
                "이메일 확인 성공. 인증 코드가 전송되었습니다.",
                new HashMap<>()
        );
        response.addData("code", code);
        return response;
    }

    public static PasswordResetResponse emailVerificationFailure() {
        return new PasswordResetResponse(
                false,
                "해당 이메일로 등록된 계정을 찾을 수 없습니다.",
                Map.of()
        );
    }

    public static PasswordResetResponse codeVerificationSuccess(String uuid) {
        PasswordResetResponse response = new PasswordResetResponse(
                true,
                "인증 코드가 확인되었습니다. 새 비밀번호를 설정해주세요.",
                Map.of()
        );
        response.addData("uuid", uuid);
        return response;
    }

    public static PasswordResetResponse codeVerificationFailure() {
        return new PasswordResetResponse(
                false,
                "잘못된 인증 코드입니다. 다시 시도해주세요.",
                Map.of()
        );
    }

    public static PasswordResetResponse uuidVerificationSuccess() {
        return new PasswordResetResponse(
                true,
                "비밀번호 재설정 페이지 uuid 유효성이 확인되었습니다.",
                Map.of()
        );
    }

    public static PasswordResetResponse uuidVerificationFailure() {
        return new PasswordResetResponse(
                false,
                "비밀번호 재설정 uuid가 유효하지 않습니다.",
                Map.of()
        );
    }
}