package esu.visionary.infrastructure.roadmap.repository;

import esu.visionary.domain.roadmap.RoadmapTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoadmapTemplateRepository extends JpaRepository<RoadmapTemplate, Long> {

    @Query("select t from RoadmapTemplate t order by t.id desc")
    List<RoadmapTemplate> findLatest(org.springframework.data.domain.Pageable pageable); // ★ FQN로 못박기
}
