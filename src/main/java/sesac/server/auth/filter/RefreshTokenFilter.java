package sesac.server.auth.filter;


import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sesac.server.account.service.TokenBlacklistService;
import sesac.server.auth.exception.TokenErrorCode;
import sesac.server.auth.exception.TokenException;
import sesac.server.common.util.JwtUtil;

@Log4j2
@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final String refreshPath = "/refreshToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter");
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        try {
            checkAccessToken(accessToken);
        } catch (TokenException tokenException) {
            tokenException.sendResponseError(response);
        }

        try {
            if (tokenBlacklistService.isBlacklisted(refreshToken)) {
                throw new TokenException(TokenErrorCode.REFRESH_TOKEN_EXPIRED);
            }

            Map<String, Object> refreshClaims = checkRefreshToken(refreshToken);

            String id = (String) refreshClaims.get("id");
            String role = (String) refreshClaims.get("role");
            String nickname = (String) refreshClaims.get("nickname");

            Map<String, Object> claim = Map.of("id", id, "role", role, "nickname", nickname);

            String accessTokenValue = jwtUtil.generateToken(claim, 1);
            String refreshTokenValue = tokens.get("refreshToken");

            // 만료 시간과 현재 시간의 간격
            // 3일 미만인 경우 Refresh 토큰 다시 생성
            Long exp = (Long) refreshClaims.get("exp");
            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());
            long gapTime = (expTime.getTime() - current.getTime());

            if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
                refreshTokenValue = jwtUtil.generateToken(claim, 30);
            }

            Map<String, Object> payload = new HashMap<>(claim);
            payload.put("accessToken", accessTokenValue);
            payload.put("refreshToken", refreshTokenValue);
            payload.remove("id");

            sendTokens(payload, response);
        } catch (TokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }
    }

    private void checkAccessToken(String accessToken) throws TokenException {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            log.info("Access Token has expired");
        } catch (Exception exception) {
            throw new TokenException(TokenErrorCode.TOKEN_UNACCEPT);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshPath) throws TokenException {
        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshPath);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new TokenException(TokenErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (MalformedJwtException malformedJwtException) {
            throw new TokenException(TokenErrorCode.TOKEN_MALFORM);
        } catch (Exception exception) {
            throw new TokenException(TokenErrorCode.TOKEN_UNACCEPT);
        }
    }

    private void sendTokens(Map<String, Object> payload, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();

        String jsonStr = gson.toJson(payload);

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
