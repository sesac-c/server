package sesac.server.common.interceptor;

import io.jsonwebtoken.JwtException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sesac.server.auth.dto.CustomPrincipal;
import sesac.server.common.util.JwtUtil;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
                StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                String token = extractToken(accessor);
                if (token != null) {
                    Authentication auth = createAuthenticationFromToken(token);
                    accessor.setUser(auth);
                    log.info("사용자에 대한 WebSocket 연결이 인증되었습니다.");
                } else {
                    throw new MessageDeliveryException("Header에서 토큰을 찾을 수 없습니다.");
                }
            } catch (JwtException e) {
                log.error("JWT 토큰 인증에 실패: {}", e.getMessage());
                throw new MessageDeliveryException("Invalid JWT token");
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader(AUTHORIZATION_HEADER);

        if (authorization != null && !authorization.isEmpty()) {
            String bearerToken = authorization.get(0);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

    public Authentication createAuthenticationFromToken(String token) {
        Map<String, Object> payload = jwtUtil.validateToken(token);

        // payload에서 id와 role 추출
        Long id = Long.valueOf(payload.get("id").toString());  // id 추출
        String role = (String) payload.get("role");

        // CustomPrincipal 생성
        CustomPrincipal customPrincipal = new CustomPrincipal(id, role);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        return new UsernamePasswordAuthenticationToken(
                customPrincipal,  // CustomPrincipal을 principal로 설정
                null,
                authorities
        );
    }
}