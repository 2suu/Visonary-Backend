package esu.visionary.domain.surveyresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResultResponse {

    private Integer surveySessionId;
    private String model;                 // "RIASEC"
    private List<Trait> traits;           // 6요인
    private String summary;               // 종합 요약

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trait {
        private String code;              // R/I/A/S/E/C
        private String label;             // 현실형/탐구형/...
        private Integer score;            // 점수
        private Integer percentile;       // 백분위(0~100)
    }
}
