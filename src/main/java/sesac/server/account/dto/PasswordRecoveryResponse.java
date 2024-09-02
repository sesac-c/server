package sesac.server.account.dto;

import java.util.HashMap;
import java.util.Map;

public record PasswordRecoveryResponse(
        boolean success,
        String message,
        Map<String, String> data
) {

    public PasswordRecoveryResponse {
        data = new HashMap<>(data);
    }

    public void addData(String key, String value) {
        this.data.put(key, value);
    }

    public static PasswordRecoveryResponse emailVerificationSuccess(String code) {
        PasswordRecoveryResponse response = new PasswordRecoveryResponse(
                true,
                "이메일 확인 성공. 인증 코드가 전송되었습니다.",
                new HashMap<>()
        );
        response.addData("code", code);
        return response;
    }

    public static PasswordRecoveryResponse emailVerificationFailure() {
        return new PasswordRecoveryResponse(
                false,
                "해당 이메일로 등록된 계정을 찾을 수 없습니다.",
                Map.of()
        );
    }

    public static PasswordRecoveryResponse codeSaveFailure() {
        return new PasswordRecoveryResponse(
                false,
                "인증 코드 저장에 실패했습니다.",
                Map.of()
        );
    }


    public static PasswordRecoveryResponse codeVerificationSuccess() {
        return new PasswordRecoveryResponse(
                true,
                "인증 코드가 확인되었습니다. 새 비밀번호를 설정해주세요.",
                Map.of()
        );
    }

    public static PasswordRecoveryResponse codeVerificationFailure() {
        return new PasswordRecoveryResponse(
                false,
                "잘못된 인증 코드입니다. 다시 시도해주세요.",
                Map.of()
        );
    }

    public static PasswordRecoveryResponse passwordResetSuccess() {
        return new PasswordRecoveryResponse(
                true,
                "비밀번호가 성공적으로 재설정되었습니다.",
                Map.of()
        );
    }

    public static PasswordRecoveryResponse passwordResetFailure() {
        return new PasswordRecoveryResponse(
                false,
                "비밀번호 재설정에 실패했습니다. 다시 시도해주세요.",
                Map.of()
        );
    }
}