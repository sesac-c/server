package sesac.server.auth.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sesac.server.account.service.TokenBlacklistService;
import sesac.server.auth.exception.TokenErrorCode;
import sesac.server.auth.exception.TokenException;
import sesac.server.common.util.JwtUtil;

@Log4j2
@RequiredArgsConstructor
@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                if (tokenBlacklistService.isBlacklisted(token)) {
                    throw new TokenException(TokenErrorCode.EXPIRED);
                }

                Map<String, Object> claim = validateAccessToken(token);

                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + claim.get("role")));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claim, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (TokenException tokenException) {
                tokenException.sendResponseError(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Map<String, Object> validateAccessToken(String tokenStr)
            throws TokenException {

        try {
            return jwtUtil.validateToken(tokenStr);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalFormedJwtException-------------");
            throw new TokenException(TokenErrorCode.MALFORM);
        } catch (SignatureException signatureException) {
            log.error("SignatureException-------------");
            throw new TokenException(TokenErrorCode.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException-------------");
            throw new TokenException(TokenErrorCode.EXPIRED);
        }
    }

}