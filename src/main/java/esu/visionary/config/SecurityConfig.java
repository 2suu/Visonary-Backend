package esu.visionary.config;

import com.google.cloud.storage.HttpMethod;
import esu.visionary.jwt.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(); // OncePerRequestFilter 구현체 가정
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 401/403 커스텀 처리기 (원한다면 간단 구현)
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}");
        };
    }

    // RN/웹에서 호출할 때 CORS 허용 (개발용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 개발 중에는 * 허용, 운영에서는 도메인 고정 권장
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Client"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true); // 쿠키 전략 쓸 때만 true
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 요청은 모두 허용
                        .requestMatchers("OPTIONS", "/**").permitAll()

                        // 인증 없이 열어둘 엔드포인트
                        .requestMatchers(
                                "/api/auth/**",          // 로그인/회원가입/재발급 등
                                "/api/survey/**",        // 설문 공개 API라면
                                "/actuator/health",      // 헬스체크
                                "/v3/api-docs/**",       // OpenAPI 문서
                                "/swagger-ui/**",        // Swagger UI
                                "/swagger-resources/**"
                        ).permitAll()

                        // 정적 리소스 (필요시)
                        .requestMatchers(
                                "/", "/index.html", "/static/**", "/assets/**"
                        ).permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 전에 JWT 검사
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        // H2 콘솔 쓰면 아래 2줄 (개발용)
        // http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        // http.authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll());

        return http.build();
    }
}
