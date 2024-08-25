package sesac.server.auth.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sesac.server.auth.dto.LoginRequest;
import sesac.server.auth.exception.AccountErrorCode;
import sesac.server.auth.exception.AccountException;
import sesac.server.auth.util.JwtUtil;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("accounts")
public class AccountController {

    private final JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // TODO: 서비스로 로직 이동
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", loginRequest.username());
        claims.put("nickname", "nickname");
        claims.put("role", "MANAGER");

        String token = jwtUtil.generateToken(claims, 1);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

    ;

    @GetMapping
    public ResponseEntity<String> getUser() {
        throw new AccountException(AccountErrorCode.NO);
    }

}
