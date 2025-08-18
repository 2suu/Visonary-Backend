// src/main/java/esu/visionary/api/survey/request/SurveyResultRequest.java
package esu.visionary.api.survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyResultRequest {

    // 기본 정보
    @JsonProperty("유저고유Id")
    private Long userId;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("나이")
    private Integer age;

    @JsonProperty("성별")
    private String gender;

    @JsonProperty("경제상황")
    private String economicStatus;

    @JsonProperty("관심분야")
    private String interest;

    @JsonProperty("최소기대임금")
    private Integer minimumExpectedSalary;

    @JsonProperty("가치관")
    private String value;

    @JsonProperty("처리여부")
    private Boolean processed;

    // createdAt은 클라이언트에서 안 보내도 됨 (서버에서 자동 세팅 가능)
    private LocalDateTime createdAt;

    /* ---------- 성향(점수) 입력 두 가지 형식 모두 지원 ---------- */

    // 1) 한글 키로 들어오는 배열 형태 (예: "성향": [{"questionId":1,"question":"...","answer":3}, ...])
    @JsonProperty("성향")
    private List<TraitAnswer> traitsList;

    // 2) 영문/맵 형태 (예: "traits": {"개방성": 3, "외향성": 4, ...})
    //   한글 키를 쓰고 싶다면 "성향점수": {...} 로도 받게 해둠
    @JsonProperty("traits")
    private Map<String, Integer> traitsMap;

    @JsonProperty("성향점수")
    private Map<String, Integer> traitsMapKo;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TraitAnswer {
        private Integer questionId;
        private String question;
        private Integer answer;
    }
}
