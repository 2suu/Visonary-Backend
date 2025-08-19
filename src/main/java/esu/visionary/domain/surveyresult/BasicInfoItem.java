package esu.visionary.domain.surveyresult;

import esu.visionary.domain.surveyresult.enums.BasicCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicInfoItem {
    @Schema(description = "항목 구분", example = "AGE",
            allowableValues = {"AGE","GENDER","EDUCATION","CAREER"})
    private BasicCategory category;

    @Schema(description = "제목", example = "나이")
    private String label;

    @Schema(description = "값", example = "23세")
    private String value;
}
