package esu.visionary.infrastructure.content.repository;

import esu.visionary.domain.content.quote.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    // 날짜(ERD의 date 컬럼)로 당일 명언 조회
    Optional<Quote> findByDate(LocalDate date);

    // 같은 날짜가 이미 있는지 체크 (유니크 보장/시드 시 유용)
    boolean existsByDate(LocalDate date);
}
