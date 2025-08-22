// src/main/java/esu/visionary/bootstrap/config/LocalSecurityConfig.java
package esu.visionary.bootstrap.config;

import esu.visionary.infrastructure.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalSecurityConfig {

    private final JwtFilter jwtFilter; // 없다면 생성자 주입 지우세요.

    @PostConstruct
    void init() {
        log.info("[SECURITY] LocalSecurityConfig LOADED (profile=local)");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 로컬에서 사용할 인코더
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 폼/기본인증/로그아웃 모두 비활성화
                .csrf(csrf -> csrf.disable())
                .httpBasic(h -> h.disable())
                .formLogin(f -> f.disable())
                .logout(l -> l.disable())

                // 세션은 사용 안 함
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 접근 권한
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/survey/**",
                                "/actuator/**",
                                "/h2-console/**"
                        ).permitAll()
                        .anyRequest().permitAll() // 로컬은 전부 오픈
                )

                // H2 콘솔 프레임 허용(사용하는 경우)
                .headers(h -> h.frameOptions(f -> f.disable()))

                // (선택) JWT 필터 추가 — 없다면 이 줄은 제거해도 됩니다.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
