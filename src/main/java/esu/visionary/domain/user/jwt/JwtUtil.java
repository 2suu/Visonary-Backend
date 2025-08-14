package esu.visionary.domain.user.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_KEY = "esu_visionary_secret_key_for_jwt_signing_2025!";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 2;     // 2시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 14; // 14일

    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Access Token 생성
    public static String generateAccessToken(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public static String generateRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검증
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 userId 추출
    public static String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 역할(role) 추출
    public static String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    // 토큰에서 Claims 추출
    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
