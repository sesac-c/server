package sesac.server.account.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.account.dto.EmailCheckRequest;
import sesac.server.account.dto.LoginRequest;
import sesac.server.account.dto.SignupRequest;
import sesac.server.account.exception.AccountBindHandler;
import sesac.server.account.service.AccountService;
import sesac.server.auth.util.JwtUtil;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("accounts")
public class AccountController {

    private final JwtUtil jwtUtil;
    private final AccountService accountService;
    private final AccountBindHandler bindHandler;

    @PostMapping("signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) {
        bindHandler.signupRequest(bindingResult);
        accountService.createStudent(signupRequest);

        return ResponseEntity.ok().build();
    }

    @PostMapping("signup/check-email")
    public ResponseEntity<Void> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        accountService.checkEmail(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // TODO: 서비스로 로직 이동
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", loginRequest.username());
        claims.put("nickname", "nickname");
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, 1);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }


}
