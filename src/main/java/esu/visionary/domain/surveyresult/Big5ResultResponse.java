package esu.visionary.domain.surveyresult;

import java.util.List;

// ApiResponse / ErrorResponse 는 기존 공통 포맷 사용 가정
public record Big5ResultResponse(
        Integer surveySessionId,
        String model,                     // "Big5"
        List<Big5Trait> personalities,    // 5요인
        String summary
) {
    public static record Big5Trait(
            String code,      // "O" | "C" | "E" | "A" | "N"
            String label,     // "개방성" 등
            Integer score,    // 0~20 가정(요구사항에 맞춰 조정)
            Integer percentile // 0~100
    ) {}
}
