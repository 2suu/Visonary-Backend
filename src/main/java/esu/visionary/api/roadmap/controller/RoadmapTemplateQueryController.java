package esu.visionary.api.roadmap.controller;

import esu.visionary.application.roadmap.service.RoadmapTemplateQueryService;
import esu.visionary.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roadmap-templates")
@Tag(name = "Roadmap Templates", description = "로드맵 템플릿 조회 API")
@RequiredArgsConstructor
@Validated
public class RoadmapTemplateQueryController {

    private final RoadmapTemplateQueryService service;

    /**
     * GET /api/roadmap-templates?count=3
     * - count 미입력 시 3
     * - 1~20 사이로 제한 → 위반 시 400
     */
    @GetMapping
    @Operation(
            summary = "로드맵 템플릿 목록 조회",
            description = """
            최신 로드맵 템플릿을 count개까지 반환합니다.
            - `count` 미입력 시 기본값 5
            - 유효 범위: 1 ~ 20
            """
    )
    public ApiResponse<?> getTemplates(
            @RequestParam(name = "count", required = false, defaultValue = "5")
            @Min(value = 1, message = "count는 1 이상이어야 합니다.")
            @Max(value = 20, message = "count 최대 값은 20입니다.")
            Integer count
    ) {
        var data = service.getTopTemplates(count);
        // ApiResponse.success(data) → {code,status,message,data} 형태로 직렬화
        return ApiResponse.success(data);
    }
}
