package sesac.server.account.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.account.dto.EmailCheckRequest;
import sesac.server.account.dto.LoginRequest;
import sesac.server.account.dto.LoginResponse;
import sesac.server.account.dto.LogoutRequest;
import sesac.server.account.dto.PasswordResetResponse;
import sesac.server.account.dto.SignupRequest;
import sesac.server.account.dto.VerifyCodeRequest;
import sesac.server.account.exception.AccountErrorCode;
import sesac.server.account.service.AccountService;
import sesac.server.auth.dto.AuthPrincipal;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.exception.BindingResultHandler;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("accounts")
public class AccountController {

    private final AccountService accountService;
    private final BindingResultHandler bindingResultHandler;

    @PostMapping("signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) {

        bindingResultHandler.handleBindingResult(bindingResult, List.of(
                AccountErrorCode.REQUIRED_NAME,
                AccountErrorCode.INVALID_NAME_PATTERN,
                AccountErrorCode.INVALID_NAME_SIZE,
                AccountErrorCode.REQUIRED_BIRTH,
                AccountErrorCode.INVALID_BIRTH_PATTERN,
                AccountErrorCode.INVALID_BIRTH_SIZE,
                AccountErrorCode.REQUIRED_GENDER,
                AccountErrorCode.INVALID_GENDER,
                AccountErrorCode.REQUIRED_EMAIL,
                AccountErrorCode.INVALID_EMAIL_PATTERN,
                AccountErrorCode.REQUIRED_PASSWORD,
                AccountErrorCode.INVALID_PASSWORD_PATTERN,
                AccountErrorCode.REQUIRED_PASSWORD_CONFIRM
        ));

        accountService.createStudent(signupRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("signup/check-email")
    public ResponseEntity<Void> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        accountService.checkEmail(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = accountService.login(loginRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        accountService.logout(logoutRequest.accessToken());
        accountService.logout(logoutRequest.refreshToken());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("withdraw")
    public ResponseEntity<Void> deleteUser(@AuthPrincipal CustomPrincipal principal) {
        accountService.deleteUser(principal.id());

        return ResponseEntity.ok().build();
    }

    @PostMapping("find-password")
    public ResponseEntity<PasswordResetResponse> checkEmailAndSendCode(
            @Valid @RequestBody EmailCheckRequest request) throws Exception {
        PasswordResetResponse response = accountService.checkEmailAndGenerateCode(
                request.email());
        return ResponseEntity.ok().body(response);
    }
    
    @PostMapping("find-password/verify/code")
    public ResponseEntity<PasswordResetResponse> validateCodeAndRedirectToResetPage(
            @RequestBody VerifyCodeRequest request) throws Exception {
        PasswordResetResponse response = accountService.validateCodeAndGeneratePasswordResetUrl(
                request.email(), request.code());
        return ResponseEntity.ok().body(response);
    }
}
