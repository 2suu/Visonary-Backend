package esu.visionary.domain.quote;

import esu.visionary.domain.content.quote.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private Long quotesId;
    private String content;
    private String author;

    public static QuoteResponse from(Quote entity) {
        return QuoteResponse.builder()
                .quotesId(entity.getId())
                .content(entity.getContent())
                .author(entity.getAuthor())
                .build();
    }
}
