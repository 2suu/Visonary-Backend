package esu.visionary.api.surveyresult;

import esu.visionary.application.surveyresult.SurveyBasicInfoService;
import esu.visionary.domain.surveyresult.BasicInfoCareerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/survey-sessions")
@RequiredArgsConstructor
@Validated
@Tags({
        @Tag(name = "Survey Result", description = "설문 결과 조회 API"),
})
public class SurveyBasicInfoController {

    private final SurveyBasicInfoService service;

    @GetMapping(value = "/{surveySessionId}/results/basic-info-career",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "기본 정보/경력 조회",
            description = "설문 세션 ID를 기반으로 나이, 성별, 학력, 경력 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "성공 예시", value = """
                {
                  "code": 200,
                  "status": "OK",
                  "message": "SUCCESS",
                  "data": [
                    { "category": "AGE", "label": "나이", "value": "23세" },
                    { "category": "GENDER", "label": "성별", "value": "남자" },
                    { "category": "EDUCATION", "label": "최종 학력", "value": "4년제 대학교 재학" },
                    { "category": "CAREER", "label": "경력", "value": "신입" }
                  ]
                }
            """))),
            @ApiResponse(responseCode = "404", description = "세션/결과 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "404 예시", value = """
                { "code": 404, "status": "NOT_FOUND", "message": "해당 설문 세션 결과가 존재하지 않습니다." }
            """))),
            @ApiResponse(responseCode = "410", description = "세션 만료/취소",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "410 예시", value = """
                { "code": 410, "status": "GONE", "message": "세션이 만료되었거나 취소되었습니다." }
            """)))
    })
    public ResponseEntity<BasicInfoCareerResponse> getBasicInfoCareer(
            @PathVariable("surveySessionId") Long surveySessionId) {

        BasicInfoCareerResponse response = service.getBasicInfoCareer(surveySessionId);
        return ResponseEntity.ok(response);
    }
}
