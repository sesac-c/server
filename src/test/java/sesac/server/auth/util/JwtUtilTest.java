package sesac.server.auth.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void testGenerateToken() {
        Map<String, Object> claimMap = Map.of("id", "aaa");
        String jwtStr = jwtUtil.generateToken(claimMap, 1);

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
    }

    @Test
    public void testIsExpired() {
        Map<String, Object> claimMap = Map.of("id", "aaa");
        //유효시간이 지난 토큰
        String jwtStr = jwtUtil.generateToken(claimMap, 0);

        // 토큰 유효 시간만료
        Assertions.assertThrows(ExpiredJwtException.class,
                () -> jwtUtil.validateToken(jwtStr));
    }

    @Test
    public void isInvalidJwtToken() {
        Map<String, Object> claimMap = Map.of("id", "aaa");
        String jwtStr = jwtUtil.generateToken(claimMap, 1);

        // 토큰 문자열의 마지막 부분에 임의의 문자를 추가
        Assertions.assertThrows(SignatureException.class,
                () -> jwtUtil.validateToken(jwtStr + "123"));
    }

    @Test
    public void testAll() {
        String jwtStr = jwtUtil.generateToken(Map.of("id", "aaa", "email", "aaaa@bbb.com"), 1);
        log.info(jwtStr);

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);
        log.info("ID: " + claim.get("id"));
        log.info("EMAIL: " + claim.get("email"));
    }

}