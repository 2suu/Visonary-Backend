package esu.visionary.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 토큰 검증을 건너뛸(permitAll) 경로 패턴들 */
    private static final List<String> SKIP_PATTERNS = List.of(
            "/api/auth/**",   // 로그인/회원가입/인증
            "/api/survey/**", // 설문
            "/actuator/**",
            "/h2-console/**",
            "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1) 화이트리스트 경로는 완전 스킵
        if (shouldSkip(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 토큰 추출
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 토큰이 없으면 Security의 AuthenticationEntryPoint/AccessDeniedHandler에서 처리
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (!JwtUtil.validateToken(token)) {
                unauthorized(response, "Invalid JWT Token");
                return;
            }

            Claims claims = JwtUtil.getClaims(token);
            String userId = claims.getSubject();
            Object role = claims.get("role");

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 컨트롤러에서 사용할 수 있도록 request attribute 추가
            request.setAttribute("userId", userId);
            request.setAttribute("role", role);

            chain.doFilter(request, response);

        } catch (JwtException ex) {
            unauthorized(response, "Token validation failed: " + ex.getMessage());
        }
    }

    private boolean shouldSkip(String uri) {
        for (String pattern : SKIP_PATTERNS) {
            if (PATH_MATCHER.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private void unauthorized(HttpServletResponse resp, String message) throws IOException {
        SecurityContextHolder.clearContext();
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(message);
    }
}
