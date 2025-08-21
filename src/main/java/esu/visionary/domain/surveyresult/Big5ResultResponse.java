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
public class Big5ResultResponse {

    private Integer surveySessionId;
    private String model;                     // "Big5"
    private List<Big5Trait> personalities;    // 5요인
    private String summary;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Big5Trait {
        private String code;        // "O" | "C" | "E" | "A" | "N"
        private String label;       // "개방성" 등
        private Integer score;      // 0~20 가정
        private Integer percentile; // 0~100
    }
}
