package esu.visionary.application.surveyresult;

import esu.visionary.common.exception.GoneException;
import esu.visionary.common.exception.NotFoundException;
import esu.visionary.domain.surveyresult.Big5ResultResponse;
import esu.visionary.infrastructure.surveyresult.SurveyResultFirebaseRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Big5ResultService {

    private final SurveyResultFirebaseRepository repo;

    public Big5ResultService(SurveyResultFirebaseRepository repo) {
        this.repo = repo;
    }

    public Big5ResultResponse getBig5Result(Integer surveySessionId) {

        if (surveySessionId == null || surveySessionId <= 0) {
            throw new BadRequestException("잘못된 요청 형식입니다.");
        }

        Map<String, Object> doc = repo.getSessionDoc(surveySessionId)
                .orElseThrow(() -> new NotFoundException("해당 설문 세션 결과가 존재하지 않습니다."));

        String status = optString(doc.get("status"));
        if (isGoneStatus(status)) {
            throw new GoneException("세션이 만료되었거나 취소되었습니다.");
        }

        // ui_summary.big5.summary
        String summary = nestedString(doc, List.of("ui_summary", "big5", "summary"));

        // ui_summary.big5.bars (List<Map>)
        List<Map<String, Object>> bars = nestedListOfMap(doc, List.of("ui_summary", "big5", "bars"));

        List<Big5ResultResponse.Big5Trait> traits = new ArrayList<>();
        if (bars != null) {
            for (Map<String, Object> b : bars) {
                String label = optString(b.get("label"));
                String code = optString(b.get("code"));
                Integer score = optInt(b.get("score"));
                Integer percentile = optInt(b.get("percent")); // 요청서: percentile ← bars[i].percent

                // code가 없을 수도 있으므로 label→code 매핑 보정
                if (code == null || code.isBlank()) {
                    code = labelToCode(label); // e.g., 개방성→O
                }

                traits.add(new Big5ResultResponse.Big5Trait(
                        code, label, score, percentile
                ));
            }
        }

        return new Big5ResultResponse(
                surveySessionId,
                "Big5",
                traits,
                summary
        );
    }

    private static boolean isGoneStatus(String status) {
        if (status == null) return false;
        return switch (status) {
            case "EXPIRED", "CANCELLED" -> true;
            default -> false;
        };
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

    // ------- 안전한 nested 접근 유틸 --------
    @SuppressWarnings("unchecked")
    private static String nestedString(Map<String, Object> root, List<String> path) {
        Object cur = root;
        for (String p : path) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return (cur instanceof String) ? (String) cur : null;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> nestedListOfMap(Map<String, Object> root, List<String> path) {
        Object cur = root;
        for (String p : path) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        if (cur instanceof List<?> list) {
            List<Map<String, Object>> out = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    out.add((Map<String, Object>) m);
                }
            }
            return out;
        }
        return null;
    }

    private static String optString(Object o) {
        return (o instanceof String s && !s.isBlank()) ? s : null;
    }

    private static Integer optInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        try { return (o != null) ? Integer.parseInt(o.toString()) : null; } catch (Exception ignored) {}
        return null;
    }
}
