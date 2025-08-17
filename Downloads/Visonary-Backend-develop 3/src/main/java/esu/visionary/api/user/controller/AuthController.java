package esu.visionary.api.user.controller;

import esu.visionary.api.user.request.SignupRequest;
import esu.visionary.api.user.request.VerifyRequest;
import esu.visionary.api.user.request.LoginRequest;
import esu.visionary.infrastructure.security.jwt.JwtUtil;
import esu.visionary.domain.user.model.User;
import esu.visionary.application.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final Map<String, String> authCodeMap = new HashMap<>();
    private final Set<String> verifiedTransactionIds = new HashSet<>();

    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;

    // 디버그용: 매핑이 살아있는지 확인
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("ok", true, "at", new Date().toString());
    }

    // 1. 본인 인증 요청
    @PostMapping("/request")
    public ResponseEntity<?> sendVerificationCode(@RequestBody VerifyRequest request) {
        String transactionId = UUID.randomUUID().toString();
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        authCodeMap.put(transactionId, code);

        System.out.printf("🔐 인증번호 전송됨 [%s] → 코드: %s%n", transactionId, code);
        System.out.println("▶ 이름: " + request.getName());
        System.out.println("▶ 주민번호: " + request.getIdentificationNumber());
        System.out.println("▶ 전화번호: " + request.getPhoneNumber());
        System.out.println("▶ 통신사: " + request.getCarrier());
        System.out.println("▶ 성별: " + request.getGender());

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "인증번호가 전송되었습니다.",
                "transactionId", transactionId
        ));
    }

    // 2. 본인 인증 확인
    @PostMapping("/verify")
    public ResponseEntity<?> confirmVerification(@RequestBody Map<String, String> body) {
        String transactionId = body.get("transactionId");
        String verificationCode = body.get("verificationCode");

        if (transactionId == null || verificationCode == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "입력값이 누락되었습니다."
            ));
        }

        String savedCode = authCodeMap.get(transactionId);
        if (savedCode != null && savedCode.equals(verificationCode)) {
            verifiedTransactionIds.add(transactionId);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "본인 인증이 완료되었습니다.",
                    "certified", true
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "message", "인증번호가 일치하지 않습니다.",
                    "certified", false
            ));
        }
    }

    // 3. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (!verifiedTransactionIds.contains(request.getTransactionId())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "본인 인증이 필요합니다."
            ));
        }

        try {
            if (userService.existsById(request.getId())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "message", "이미 사용 중인 아이디입니다."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "아이디 중복 확인 중 오류가 발생했습니다."
            ));
        }

        User user = new User(
                (long) (new Random().nextInt(9000) + 1000), // 1000~9999
                request.getId(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickName()
        );

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "회원 정보 저장 중 오류가 발생했습니다."
            ));
        }

        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "회원가입 성공"
        ));
    }

    // 4. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user;
        try {
            user = userService.getUserById(request.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "사용자 조회 중 오류가 발생했습니다."
            ));
        }

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "message", "아이디 또는 비밀번호가 잘못되었습니다."
            ));
        }

        String accessToken = JwtUtil.generateAccessToken(user.getId(), "ROLE_USER");
        String refreshToken = JwtUtil.generateRefreshToken();

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "로그인 성공",
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "userInfo", Map.of(
                        "userId", user.getUserId(),
                        "id", user.getId(),
                        "nickName", user.getNickName()
                )
        ));
    }

    // 5. 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "로그아웃 성공"
        ));
    }

    // 6. 아이디 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam String id) {
        boolean isDuplicated;
        try {
            isDuplicated = userService.existsById(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "중복 확인 중 오류 발생"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", isDuplicated ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.",
                "data", Map.of("isDuplicated", isDuplicated, "id", id)
        ));
    }

    // 7-1. 닉네임 중복 확인 (쿼리스트링)
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam(name = "nickname") String nickname) {
        return checkNicknameInternal(nickname);
    }

    // 7-2. 닉네임 중복 확인 (패스 변수) - /api/auth/check-nickname/홍길동
    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<?> checkNicknamePath(@PathVariable("nickname") String nickname) {
        return checkNicknameInternal(nickname);
    }

    private ResponseEntity<?> checkNicknameInternal(String nickname) {
        boolean isDuplicated;
        try {
            isDuplicated = userService.existsByNickname(nickname);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "중복 확인 중 오류 발생"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", isDuplicated ? "이미 사용 중인 닉네임입니다." : "사용 가능한 닉네임입니다.",
                "data", Map.of("isDuplicated", isDuplicated, "nickname", nickname)
        ));
    }
}
