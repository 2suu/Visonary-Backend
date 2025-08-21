package esu.visionary.application.surveyresult;

import esu.visionary.common.exception.BadRequestException;
import esu.visionary.common.exception.GoneException;
import esu.visionary.common.exception.NotFoundException;
import esu.visionary.domain.surveyresult.InterestResultResponse;
import esu.visionary.infrastructure.surveyresult.SurveyResultFirebaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterestResultService {

    private final SurveyResultFirebaseRepository repo;

    public InterestResultResponse getInterests(Integer surveySessionId) {
        log.info("[RIASEC] START getInterests sessionId={}", surveySessionId);

        if (surveySessionId == null || surveySessionId <= 0) {
            log.warn("[RIASEC] INVALID sessionId={}", surveySessionId);
            throw new BadRequestException("잘못된 요청 형식입니다.");
        }

        // 1) 루트 문서 조회
        Map<String, Object> root = repo.getSessionDoc(surveySessionId)
                .orElseThrow(() -> {
                    log.warn("[RIASEC] NOT_FOUND sessionId={} (root doc 없음)", surveySessionId);
                    return new NotFoundException("해당 설문 세션 결과가 존재하지 않습니다.");
                });

        if (log.isDebugEnabled()) log.debug("[RIASEC] root keys={}", root.keySet());

        // 2) 상태 확인
        String status = optString(root.get("status"));
        log.debug("[RIASEC] status={}", status);
        if (isGoneStatus(status)) {
            log.warn("[RIASEC] GONE sessionId={} status={}", surveySessionId, status);
            throw new GoneException("세션이 만료되었거나 취소되었습니다.");
        }

        // 3) 핵심 경로: interests_analysis.summary / interests_analysis.bars
        String summary = clean(nestedString(root, List.of("interests_analysis", "summary")));
        if (summary == null) {
            // (선택) 최소 폴백: 기존 ui_summary.riasec.summary → riasec_analysis.summary
            log.debug("[RIASEC] summary not found at interests_analysis.summary → fallback");
            summary = firstNonNull(
                    clean(nestedString(root, List.of("ui_summary", "riasec", "summary"))),
                    clean(nestedString(root, List.of("riasec_analysis", "summary")))
            );
        }
        log.debug("[RIASEC] summary exists? {}", summary != null);

        List<Map<String, Object>> bars = nestedListOfMap(root, List.of("interests_analysis", "bars"));
        String barsSource = "interests_analysis.bars";
        if (bars == null) {
            // (선택) 최소 폴백
            log.debug("[RIASEC] bars not found at interests_analysis.bars → fallback");
            bars = firstNonNull(
                    nestedListOfMap(root, List.of("ui_summary", "riasec", "bars")),
                    nestedListOfMap(root, List.of("riasec_analysis", "bars"))
            );
            barsSource = (bars != null) ? "fallback(ui_summary.riasec|riasec_analysis).bars" : barsSource;
        }
        log.debug("[RIASEC] bars source={} size={}", barsSource, (bars == null ? null : bars.size()));
        if (log.isTraceEnabled() && bars != null) {
            log.trace("[RIASEC] bars preview[0..2]={}", previewBars(bars, 3));
        }

        // 4) 변환
        List<InterestResultResponse.Trait> traits = new ArrayList<>();
        if (bars != null) {
            for (int i = 0; i < bars.size(); i++) {
                Map<String, Object> b = bars.get(i);
                String label = clean(optString(b.get("label")));
                String code  = clean(optString(b.get("code")));
                Integer score = optInt(b.get("score"));
                Integer percentile = optInt(b.get("percent")); // 키: percent

                if (isBlank(code)) code = labelToRiasecCode(label);

                if (log.isTraceEnabled()) {
                    log.trace("[RIASEC] trait[{}] code={} label={} score={} percentile={}",
                            i, code, label, score, percentile);
                }

                traits.add(InterestResultResponse.Trait.builder()
                        .code(code).label(label).score(score).percentile(percentile)
                        .build());
            }
        } else {
            log.warn("[RIASEC] bars is NULL (핵심 경로 및 폴백 모두 실패)");
        }

        InterestResultResponse res = InterestResultResponse.builder()
                .surveySessionId(surveySessionId)
                .model("RIASEC")
                .traits(traits)
                .summary(summary)
                .build();

        log.info("[RIASEC] END sessionId={} traits={} summary?={}",
                surveySessionId, traits.size(), summary != null);
        return res;
    }

    // ---------- helpers ----------
    private static boolean isGoneStatus(String status) {
        if (status == null) return false;
        return switch (status) { case "EXPIRED", "CANCELLED" -> true; default -> false; };
    }

    private static String labelToRiasecCode(String label) {
        if (label == null) return null;
        return switch (label) {
            case "현실형", "Realistic" -> "R";
            case "탐구형", "Investigative" -> "I";
            case "예술형", "Artistic" -> "A";
            case "사회형", "Social" -> "S";
            case "기업형", "Enterprising" -> "E";
            case "관습형", "Conventional" -> "C";
            default -> null;
        };
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... c) { for (T x : c) if (x != null) return x; return null; }

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

    /** 값 정규화: trim + 양끝의 쌍따옴표/스마트따옴표/홑따옴표 제거 */
    private static String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() >= 2) {
            char first = s.charAt(0), last = s.charAt(s.length() - 1);
            if ((first == '"' && last == '"') || (first == '“' && last == '”') || (first == '\'' && last == '\'')) {
                s = s.substring(1, s.length() - 1).trim();
            }
        }
        return s;
    }

    private static List<Map<String, Object>> previewBars(List<Map<String, Object>> bars, int limit) {
        int end = Math.min(bars.size(), Math.max(0, limit));
        return new ArrayList<>(bars.subList(0, end));
    }
}
