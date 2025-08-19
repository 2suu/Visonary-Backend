package esu.visionary.domain.surveyresult;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;


@Getter @Builder @AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "기본 정보/경력 조회 응답")
public class BasicInfoCareerResponse {
    @Schema(description = "HTTP 상태 코드 숫자", example = "200")
    private int code;

    @Schema(description = "상태 문자열", example = "OK")
    private String status;

    @Schema(description = "응답 메시지", example = "SUCCESS")
    private String message;

    @ArraySchema(arraySchema = @Schema(description = "조회된 데이터 목록"),
            schema = @Schema(implementation = BasicInfoItem.class))
    private java.util.List<BasicInfoItem> data;
}