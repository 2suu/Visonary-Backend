package esu.visionary.api.survey.controller;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
@Slf4j
public class SurveyController {

    // Firestore를 쓰지 않는 환경이면 Optional.empty()로 주입하면 됩니다.
    private final Optional<Firestore> firestoreOpt;

    /** 설문 저장 (맵 구조 + 소수 둘째 자리 반올림) */
    @PostMapping("/submit")
    public ResponseEntity<?> saveSurveyResult(@RequestBody Map<String, Object> body) {
        // 0) userId 필수
        String userId = str(pick(body, "userId", "uid", "user_id"));
        if (!StringUtils.hasText(userId)) {
            return ResponseEntity.badRequest().body(Map.of("status", 400, "message", "userId는 필수입니다."));
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("userId", userId);
        out.put("createdAt", Date.from(Instant.now()));

        // 1) 나이
        Map<String, Object> ageMap = asMap(body.get("나이"));
        Map<String, Object> ageOut = new LinkedHashMap<>();
        putIfNotNull(ageOut, "priority", intOrNull(ageMap.get("priority")));
        putIfNotNull(ageOut, "value", intOrNull(ageMap.get("value")));
        if (!ageOut.isEmpty()) out.put("나이", ageOut);

        // 2) 성별
        Map<String, Object> genderMap = asMap(body.get("성별"));
        Map<String, Object> genderOut = new LinkedHashMap<>();
        putIfNotNull(genderOut, "priority", intOrNull(genderMap.get("priority")));
        putIfNotNull(genderOut, "value", intOrNull(genderMap.get("value"))); // 1남 2여 3밝히지않음
        if (!genderOut.isEmpty()) out.put("성별", genderOut);

        // 3) 최종학력: {value:4} 또는 숫자 4
        Object eduRaw = body.get("최종학력");
        Integer education = null;
        if (eduRaw instanceof Number n) {
            education = n.intValue();
        } else if (eduRaw instanceof Map<?,?> m) {
            education = intOrNull(m.get("value"));
        }
        putIfNotNull(out, "최종학력", education);

        // 4) 경력
        Map<String, Object> career = asMap(body.get("경력"));
        Boolean hasCareer = boolOrNull(career.get("value"));
        if (hasCareer != null) {
            Map<String, Object> careerOut = new LinkedHashMap<>();
            careerOut.put("value", hasCareer);
            if (Boolean.TRUE.equals(hasCareer)) {
                putIfNotBlank(careerOut, "분야", str(career.get("분야")));
                putIfNotNull(careerOut, "기간", intOrNull(career.get("기간"))); // "24" → 24
            }
            out.put("경력", careerOut);
        }

        // 5) 경제상황 및 대인관계 (맵으로 묶기)
        Map<String, Object> econ = asMap(body.get("경제상황 및 대인관계"));
        Map<String, Object> econOut = new LinkedHashMap<>();
        putIfNotNull(econOut, "priority", intOrNull(econ.get("priority")));
        putIfNotNull(econOut, "저축", intOrNull(econ.get("저축")));
        putIfNotNull(econOut, "생활비", intOrNull(econ.get("생활비")));
        putIfNotNull(econOut, "인간관계", coalesceInt(econ.get("인간관계"), econ.get("대인관계")));
        putIfNotNull(econOut, "관계 스타일", intOrNull(econ.get("관계 스타일")));
        if (!econOut.isEmpty()) out.put("경제상황 및 대인관계", econOut);

        // 6) 감정기복/스트레스해소 (숫자)
        putIfNotNull(out, "감정기복", intOrNull(body.get("감정기복")));
        putIfNotNull(out, "스트레스해소", intOrNull(body.get("스트레스해소")));

        // 7) 성격(빅5): 배열 평균 → 소수 둘째 자리 반올림, 맵으로 묶기
        Map<String, Object> personality = asMap(body.get("성격"));
        Map<String, Object> personalityOut = new LinkedHashMap<>();
        putIfNotNull(personalityOut, "priority", intOrNull(personality.get("priority")));
        putIfNotNull(personalityOut, "외향성", avgRounded2(personality.get("외향성")));
        putIfNotNull(personalityOut, "친화성", avgRounded2(personality.get("친화성")));
        putIfNotNull(personalityOut, "성실성", avgRounded2(personality.get("성실성")));
        putIfNotNull(personalityOut, "신경성", avgRounded2(personality.get("신경성")));
        putIfNotNull(personalityOut, "개방성", avgRounded2(personality.get("개방성")));
        if (!personalityOut.isEmpty()) out.put("성격", personalityOut);

        // 8) 흥미(RIASEC): 배열 평균 → 소수 둘째 자리 반올림, 맵으로 묶기
        Map<String, Object> interest = asMap(body.get("흥미"));
        Map<String, Object> interestOut = new LinkedHashMap<>();
        putIfNotNull(interestOut, "priority", intOrNull(interest.get("priority")));
        putIfNotNull(interestOut, "실재형", avgRounded2(interest.get("실재형")));
        putIfNotNull(interestOut, "탐구형", avgRounded2(interest.get("탐구형")));
        putIfNotNull(interestOut, "예술형", avgRounded2(interest.get("예술형")));
        putIfNotNull(interestOut, "사회형", avgRounded2(interest.get("사회형")));
        putIfNotNull(interestOut, "기업형", avgRounded2(interest.get("기업형")));
        putIfNotNull(interestOut, "관습형", avgRounded2(interest.get("관습형")));
        if (!interestOut.isEmpty()) out.put("흥미", interestOut);

        // 9) 관심분야: 객체로 묶기
        Object interestsRaw = body.get("관심분야");
        Integer interestsPriority = null;
        List<String> interestsList;

        if (interestsRaw instanceof Map<?,?> im) {
            interestsPriority = intOrNull(im.get("priority"));
            interestsList = toStringList(im.get("value"));
        } else {
            interestsList = toStringList(interestsRaw);
        }

// value를 배열 → 콤마로 join된 문자열로 변환
        String interestsValue = interestsList == null || interestsList.isEmpty()
                ? ""
                : String.join(", ", interestsList);

        Map<String, Object> interestsOut = new LinkedHashMap<>();
        if (interestsPriority != null) interestsOut.put("priority", interestsPriority);
        interestsOut.put("value", interestsValue);

        out.put("관심분야", interestsOut);


        // 10) 최소급여
        putIfNotNull(out, "최소급여", intOrNull(body.get("최소급여")));

        // 저장(Firestore 예시)
        firestoreOpt.ifPresent(fs -> {
            try {
                WriteResult wr = fs.collection("survey_results")
                        .document(userId)      // 문서 ID로 userId 사용
                        .set(out)
                        .get();
                log.info("[FIRESTORE] survey_results/{} upsert at {}", userId, wr.getUpdateTime());
            } catch (InterruptedException | ExecutionException e) {
                log.error("[FIRESTORE] write failed", e);
                Thread.currentThread().interrupt();
            }
        });

        return ResponseEntity.ok(Map.of("status", 200, "message", "저장 완료", "data", out));
    }

    /* ================= helpers ================= */

    private Map<String, Object> asMap(Object o) {
        if (o instanceof Map<?,?> m) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) out.put(String.valueOf(e.getKey()), e.getValue());
            return out;
        }
        return new LinkedHashMap<>();
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) map.put(key, value);
    }

    private void putIfNotBlank(Map<String, Object> map, String key, String value) {
        if (value != null && !value.isBlank()) map.put(key, value);
    }

    private String str(Object o) { return (o == null) ? null : String.valueOf(o); }

    private Integer intOrNull(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o).trim()); }
        catch (Exception e) { return null; }
    }

    private Boolean boolOrNull(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        String s = String.valueOf(o).trim().toLowerCase(Locale.ROOT);
        return switch (s) {
            case "true","1","y","yes" -> Boolean.TRUE;
            case "false","0","n","no" -> Boolean.FALSE;
            default -> null;
        };
    }

    private Integer coalesceInt(Object... cands) {
        for (Object c : cands) {
            Integer v = intOrNull(c);
            if (v != null) return v;
        }
        return null;
    }

    /** 배열 평균 → 소수 둘째 자리 반올림(Double) */
    private Double avgRounded2(Object arr) {
        if (arr instanceof List<?> list && !list.isEmpty()) {
            double sum = 0; int cnt = 0;
            for (Object o : list) {
                Integer v = intOrNull(o);
                if (v != null) { sum += v; cnt++; }
            }
            if (cnt > 0) {
                double avg = sum / cnt;
                return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }
        return null;
    }

    /** Object → List<String> (배열/단일/쉼표구분 문자열 모두 지원) */
    private List<String> toStringList(Object o) {
        if (o == null) return Collections.emptyList();
        if (o instanceof List<?> l) {
            List<String> out = new ArrayList<>();
            for (Object e : l) if (e != null) out.add(String.valueOf(e));
            return out;
        }
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) return Collections.emptyList();
        if (s.contains(",")) {
            String[] parts = s.split(",");
            List<String> out = new ArrayList<>();
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) out.add(t);
            }
            return out;
        }
        return List.of(s);
    }

    private Object pick(Map<String, Object> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v != null) return v;
        }
        return null;
    }
}
