package sesac.server.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class JwtTestUtil {

    private static final String key = "developmentjwtkeydmdoiwj2398usd7ayf987124gugszdc9783287gf";

    public static String createTestJwtToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60)) // 1 minute expiration
                .signWith(SignatureAlgorithm.HS256, key.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public static MockHttpServletRequestBuilder addJwtToken(MockHttpServletRequestBuilder request,
            String token) {
        return request.header("Authorization", "Bearer " + token);
    }
}
