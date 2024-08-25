package sesac.server.auth.filter;

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
import sesac.server.auth.util.JwtUtil;

@Log4j2
@RequiredArgsConstructor
@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/accounts")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);

        // TODO: 수정필요
        if (token != null) {
            Map<String, Object> claim = jwtUtil.validateToken(token);

            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + claim.get("role")));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claim, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Access token: {}", SecurityContextHolder.getContext().getAuthentication());
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

    /*private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization");

        if (headerStr == null || headerStr.length() < 8) {
//            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) {
//            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            return jwtUtil.validateToken(tokenStr);
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalFormedJwtException-------------");
//            throw new AccessTokenException((AccessTokenException.TOKEN_ERROR.MALFORM));
        } catch (SignatureException signatureException) {
            log.error("SignatureException-------------");
//            throw new AccessTokenException((AccessTokenException.TOKEN_ERROR.BADSIGN));
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException-------------");
//            throw new AccessTokenException((AccessTokenException.TOKEN_ERROR.EXPIRED));
        }
        return null;
    }*/


}