package esu.visionary.api.content.controller;

import esu.visionary.application.content.QuoteService;
import esu.visionary.common.response.ApiResponse;
import esu.visionary.domain.quote.QuoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quotes")
@Tag(name = "Quotes", description = "명언 조회 API")
public class QuoteController {

    private final QuoteService quoteService;
    public QuoteController(QuoteService quoteService) { this.quoteService = quoteService; }

    @GetMapping
    @Operation(summary = "오늘의 명언 조회",
            description = "쿼리 파라미터 없이 호출 시, 한국 시간 기준 오늘 날짜로 결정된 명언 1개를 반환합니다.")
    public ResponseEntity<ApiResponse<QuoteResponse>> getToday() {
        QuoteResponse data = quoteService.getTodayQuote();
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
