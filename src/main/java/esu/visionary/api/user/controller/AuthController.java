package esu.visionary.api.user.controller;

import esu.visionary.api.user.request.SignupRequest;
import esu.visionary.api.user.request.LoginRequest;
import esu.visionary.infrastructure.security.jwt.JwtUtil;
import esu.visionary.domain.user.model.User;
import esu.visionary.application.user.service.UserService;
import esu.visionary.common.sms.SmsService;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class AuthController {

    private final Map<String, String> authCodeMap = new HashMap<>();
    private final Set<String> verifiedTransactionIds = new HashSet<>();

    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private SmsService smsService;

    // Firestore 사용 시만 주입
    @Autowired(required = false) private Firestore firestore;

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("ok", true, "at", new Date().toString());
    }

    // 1) 인증번호 요청 — 프론트 키 네이밍이 달라도 안전하게 매핑
    @PostMapping("/request")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, Object> body) {
        log.info("[/api/auth/request] incoming keys: {}", body.keySet());

        String name  = pick(body, "name", "이름");
        String idNum = pick(body,
                "identificationNumber", "identification_number", "identification-number",
                "identification", "idNumber", "id_number", "id-number",
                "residentNumber", "resident_number", "resident-number",
                "rrn", "ssn", "jumin", "주민번호", "주민등록번호"
        );
        String phone = normalizePhone(pick(body, "phoneNumber", "phone_number", "phone", "tel", "mobile", "휴대폰", "전화번호"));
        String carrier = pick(body, "carrier", "통신사");

        if (!StringUtils.hasText(name) || !StringUtils.hasText(phone) || !StringUtils.hasText(carrier)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", "이름/전화번호/통신사는 필수입니다."
            ));
        }

        String transactionId = UUID.randomUUID().toString();
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        authCodeMap.put(transactionId, code);

        smsService.sendAuthCode(phone, code);

        log.info("""
                🔐 인증번호 전송 [{}] → 코드: {}
                이름: {}
                ▶ 주민번호: {}
                ▶ 전화번호: {}
                ▶ 통신사: {}
                """,
                transactionId,
                code,
                nvl(name, "(미입력)"),
                maskIdNumber(idNum),
                nvl(phone, "(미입력)"),
                nvl(carrier, "(미입력)")
        );

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "인증번호가 전송되었습니다.",
                "transactionId", transactionId
        ));
    }

    // 2) 인증번호 확인
    @PostMapping("/verify")
    public ResponseEntity<?> confirmVerification(@RequestBody Map<String, String> body) {
        String transactionId = body.getOrDefault("transactionId", null);
        String verificationCode = body.getOrDefault("verificationCode", null);

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

    // 3) 회원가입 (필요 시 Firestore 쓰기 포함)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest request) {
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
            log.error("[SIGNUP] existsById error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "아이디 중복 확인 중 오류가 발생했습니다."
            ));
        }

        // 1차 저장brew install --cask temurin21
        User user = User.builder()
                .loginId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickName(request.getNickName())
                .build();

        try {
            user = userService.saveUser(user); // INSERT
            log.info("[SIGNUP] after insert: loginId={}, nickName={}",
                    user.getLoginId(), user.getNickName());
        } catch (Exception e) {
            log.error("[SIGNUP] insert error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "회원 정보 저장 중 오류가 발생했습니다."
            ));
        }

        // 4자리 userId 생성 & UPDATE
        String userId4 = null;
        final int MAX_TRY = 10;
        for (int i = 0; i < MAX_TRY; i++) {
            String c = String.valueOf(1000 + new Random().nextInt(9000));
            if (!userService.existsByUserId(c)) { userId4 = c; break; }
        }
        if (userId4 == null) {
            log.error("[SIGNUP] failed to generate unique userId(4)");
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "일시적인 문제로 코드 생성에 실패했습니다."
            ));
        }

        try {
            user.setUserId(userId4);          // ← userCode4 대신 userId 필드에 설정
            user = userService.saveUser(user); // UPDATE
            log.info("[SIGNUP] assigned userId(4)={} to loginId={}", user.getUserId(), user.getLoginId());
        } catch (Exception e) {
            log.error("[SIGNUP] update userId error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "회원 정보 저장 중 오류가 발생했습니다."
            ));
        }

        // (선택) Firestore에도 기록
        if (firestore != null) {
            try {
                Map<String, Object> doc = new HashMap<>();
                doc.put("loginId", user.getLoginId());
                doc.put("nickName", user.getNickName());
                doc.put("userId", user.getUserId()); // ← userId 저장
                doc.put("createdAt", new Date());

                WriteResult wr = firestore.collection("users")
                        .document(user.getLoginId())
                        .set(doc)
                        .get();
                log.info("[FIRESTORE] users/{} upsert at {}", user.getLoginId(), wr.getUpdateTime());
            } catch (Exception fe) {
                log.error("[FIRESTORE] write failed", fe);
            }
        }

        // 응답에서 pk 제거
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "message", "회원가입 성공",
                "userInfo", Map.of(
                        "userId", user.getUserId(),     // 4자리 비즈니스 ID
                        "loginId", user.getLoginId(),
                        "nickName", user.getNickName()
                )
        ));
    }

    // 4) 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user;
        try {
            // 프론트에서 보내는 id == loginId
            user = userService.getUserById(request.getId());
        } catch (Exception e) {
            log.error("[LOGIN] getUserById error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", 500,
                    "message", "사용자 조회 중 오류가 발생했습니다."
            ));
        }

        // 사용자 없음 또는 비밀번호 불일치
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "message", "아이디 또는 비밀번호가 잘못되었습니다."
            ));
        }

        // JWT subject는 loginId로 사용 중
        String accessToken = JwtUtil.generateAccessToken(user.getLoginId(), "ROLE_USER");
        String refreshToken = JwtUtil.generateRefreshToken();

        // 닉네임을 최상위 필드와 userInfo 둘 다에 담아서 반환
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "로그인 성공",
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "nickname", user.getNickName(),                // ← 바로 쓰기 좋게 최상위에
                "userInfo", Map.of(
                        "loginId", user.getLoginId(),
                        "nickname", user.getNickName(),
                        "userId", user.getUserId()             // 4자리 사용자 코드(이름 바꾼 버전)
                )
        ));
    }

    // 5) 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "로그아웃 성공"
        ));
    }

    // 6) 아이디 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam String id) {
        boolean isDuplicated;
        try {
            isDuplicated = userService.existsById(id);
        } catch (Exception e) {
            log.error("[CHECK-ID] existsById error", e);
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

    // 7) 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam(name = "nickname") String nickname) {
        return checkNicknameInternal(nickname);
    }

    @GetMapping("/check-nickname/{nickname}")
    public ResponseEntity<?> checkNicknamePath(@PathVariable("nickname") String nickname) {
        return checkNicknameInternal(nickname);
    }

    private ResponseEntity<?> checkNicknameInternal(String nickname) {
        boolean isDuplicated;
        try {
            isDuplicated = userService.existsByNickname(nickname);
        } catch (Exception e) {
            log.error("[CHECK-NICK] existsByNickname error", e);
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

    /* ───────── helpers ───────── */

    private String pick(Map<String, Object> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v != null) {
                String s = String.valueOf(v).trim();
                if (!s.isEmpty() && !"null".equalsIgnoreCase(s)) return s;
            }
        }
        return null;
    }

    private String normalizePhone(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("[^0-9]", "");
    }

    /** 주민번호 마스킹: 앞 7자리만 노출, 나머지 별표. null/빈 값은 (미입력) */
    private String maskIdNumber(String rrn) {
        if (!StringUtils.hasText(rrn)) return "(미입력)";
        String digits = rrn.replaceAll("[^0-9-]", "");
        if (digits.length() >= 7) {
            String head = digits.substring(0, Math.min(7, digits.length()));
            return head + "*".repeat(Math.max(0, digits.length() - head.length()));
        }
        return digits;
    }

    private String nvl(String v, String def) { return (v == null || v.isBlank()) ? def : v; }
}
