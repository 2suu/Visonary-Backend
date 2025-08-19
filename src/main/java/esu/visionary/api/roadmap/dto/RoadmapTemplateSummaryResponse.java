package esu.visionary.api.roadmap.dto;

import esu.visionary.domain.roadmap.RoadmapTemplate;
import lombok.Builder;

@Builder
public record RoadmapTemplateSummaryResponse(
        Long templateId,
        String title,
        String imageUrl
) {
    public static RoadmapTemplateSummaryResponse from(RoadmapTemplate t) {
        // NOTE: 엔티티의 id 게터명이 다를 수 있음(getId / getRoadmapTemplateId).
        Long id = null;
        try {
            id = (Long) RoadmapTemplate.class.getMethod("getId").invoke(t);
        } catch (Exception ignore) {
            try { id = (Long) RoadmapTemplate.class.getMethod("getRoadmapTemplateId").invoke(t); }
            catch (Exception ignored) {}
        }

        String img = null;
        try {
            img = (String) RoadmapTemplate.class.getMethod("getImageUrl").invoke(t);
        } catch (Exception ignore) {
            try { img = (String) RoadmapTemplate.class.getMethod("getImage_url").invoke(t); }
            catch (Exception ignored) {}
        }

        return RoadmapTemplateSummaryResponse.builder()
                .templateId(id)
                .title(t.getTitle())
                .imageUrl(img)
                .build();
    }
}
