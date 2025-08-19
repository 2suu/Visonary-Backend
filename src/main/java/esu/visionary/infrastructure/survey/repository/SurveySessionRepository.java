package esu.visionary.infrastructure.survey.repository;

import esu.visionary.domain.survey.SurveySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveySessionRepository extends JpaRepository<SurveySession, Long> {
    // 필요하면 커스텀 쿼리 추가
}