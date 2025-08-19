package esu.visionary.application.roadmap.service;

import esu.visionary.api.roadmap.dto.RoadmapTemplateSummaryResponse;
import esu.visionary.infrastructure.roadmap.repository.RoadmapTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapTemplateQueryService {

    private final RoadmapTemplateRepository repository;

    public List<RoadmapTemplateSummaryResponse> getTopTemplates(int count) {
        var pageable = PageRequest.of(0, count);
        return repository.findLatest(pageable)
                .stream()
                .map(RoadmapTemplateSummaryResponse::from)
                .toList();
    }
}
