package esu.visionary.application.surveyresult;

import esu.visionary.common.exception.BadRequestException;
import esu.visionary.common.exception.GoneException;
import esu.visionary.common.exception.NotFoundException;
import esu.visionary.domain.surveyresult.Big5ResultResponse;
import esu.visionary.infrastructure.surveyresult.SurveyResultFirebaseRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Big5ResultService {

    private final SurveyResultFirebaseRepository repo;

    public Big5ResultResponse getBig5Result(int surveySessionId) {
        log.info("[Big5] START getBig5Result sessionId={}", surveySessionId);

        if (surveySessionId <= 0) {
            log.warn("[Big5] INVALID sessionId={}", surveySessionId);
            throw new BadRequestException("잘못된 요청 형식입니다.");
        }

        Map<String, Object> root = repo.getSessionDoc(surveySessionId)
                .orElseThrow(() -> {
                    log.warn("[Big5] NOT_FOUND sessionId={} (root doc 없음)", surveySessionId);
                    return new NotFoundException("해당 설문 세션 결과가 존재하지 않습니다.");
                });

        String status = optString(root.get("status"));
        if (isGoneStatus(status)) {
            log.warn("[Big5] GONE sessionId={} status={}", surveySessionId, status);
            throw new GoneException("세션이 만료되었거나 취소되었습니다.");
        }

        String summary = clean(nestedString(root, List.of("big5_analysis", "summary")));
        List<Map<String, Object>> bars = nestedListOfMap(root, List.of("big5_analysis", "bars"));

        List<Big5ResultResponse.Big5Trait> traits = new ArrayList<>();
        if (bars != null) {
            for (int i = 0; i < bars.size(); i++) {
                Map<String, Object> b = bars.get(i);
                String label = clean(optString(b.get("label")));
                String code  = clean(optString(b.get("code")));
                Integer score = optInt(b.get("score"));
                Integer percentile = optInt(b.get("percent"));

                if (isBlank(code)) code = labelToCode(label);
                traits.add(new Big5ResultResponse.Big5Trait(code, label, score, percentile));
            }
        } else {
            log.warn("[Big5] big5_analysis.bars == null");
        }

        return new Big5ResultResponse(surveySessionId, "Big5", traits, summary);
    }


    // -------- utils --------
    private static boolean isGoneStatus(String status) {
        if (status == null) return false;
        return switch (status) { case "EXPIRED", "CANCELLED" -> true; default -> false; };
    }

    private static String labelToCode(String label) {
        if (label == null) return null;
        return switch (label) {
            case "개방성" -> "O";
            case "성실성" -> "C";
            case "외향성" -> "E";
            case "친화성" -> "A";
            case "신경성" -> "N";
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    private static String nestedString(Map<String, Object> root, List<String> path) {
        Object cur = root;
        for (String p : path) {
            if (!(cur instanceof Map<?, ?> m)) return null;
            cur = m.get(p);
            if (cur == null) return null;
        }
        return (cur instanceof String s) ? s : null;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> nestedListOfMap(Map<String, Object> root, List<String> path) {
        Object cur = root;
        for (String p : path) {
            if (!(cur instanceof Map<?, ?> m)) return null;
            cur = m.get(p);
            if (cur == null) return null;
        }
        if (cur instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object o : list) if (o instanceof Map<?, ?> m) out.add((Map<String, Object>) m);
            return out;
        }
        return null;
    }

    private static String optString(Object o) {
        return (o instanceof String s && !s.isBlank()) ? s : null;
    }
    private static Integer optInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        try { return (o != null) ? Integer.parseInt(o.toString()) : null; }
        catch (Exception ignored) { return null; }
    }
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    /** 값 정규화: trim + 양끝 따옴표/스마트따옴표 제거 */
    private static String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() >= 2) {
            char first = s.charAt(0), last = s.charAt(s.length()-1);
            if ((first=='"' && last=='"') || (first=='“' && last=='”') || (first=='\'' && last=='\'')) {
                s = s.substring(1, s.length()-1);
            }
        }
        return s;
    }
}
