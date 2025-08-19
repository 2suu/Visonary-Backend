package esu.visionary.application.content;

import esu.visionary.domain.content.quote.Quote;
import esu.visionary.domain.quote.QuoteResponse;
import esu.visionary.domain.quote.exception.QuoteNotFoundException;
import esu.visionary.infrastructure.content.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@Transactional(readOnly = true)
public class QuoteService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    /** 한국시간 기준 오늘의 명언 조회 */
    public QuoteResponse getTodayQuote() {
        LocalDate today = LocalDate.now(KST);
        Quote quote = quoteRepository.findByDate(today)
                .orElseThrow(() -> new QuoteNotFoundException("오늘의 명언을 찾을 수 없습니다."));
        return QuoteResponse.from(quote);
    }

    /** (선택) 임의 날짜 명언 조회가 필요할 때 사용할 수 있는 보조 메서드 */
    public QuoteResponse getQuoteByDate(LocalDate date) {
        Quote quote = quoteRepository.findByDate(date)
                .orElseThrow(() -> new QuoteNotFoundException("해당 날짜의 명언을 찾을 수 없습니다."));
        return QuoteResponse.from(quote);
    }
}
