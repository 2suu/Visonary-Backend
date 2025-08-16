package esu.visionary.bootstrap.config;

import esu.visionary.infrastructure.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // 비밀번호 암호화

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }//로그인할 때 유저 정보 검사 가능

    // Security 필터 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (브라우저 기반이 아닌 JWT 기반이므로)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 세션 사용안하고 매번 JWT토큰으로 인증할거임
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()        // 본인 인증/회원가입 허용
                        .requestMatchers("/api/survey/**").permitAll()      // 설문 API도 허용
                        .requestMatchers("/api/users/**").permitAll() // 추가
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/health", "/", "/api/**").permitAll()
                        .anyRequest().authenticated()                       // 그 외는 인증 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // 스프링 기본 필터보다 JWT 필터가 먼저 작동될 수 있게함

        return http.build();
    }
}
