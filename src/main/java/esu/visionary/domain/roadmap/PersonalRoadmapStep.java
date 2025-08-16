package esu.visionary.domain.roadmap;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_roadmap_steps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PersonalRoadmapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_roadmap_step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_roadmap_id", nullable = false) // FK → personal_roadmaps.personal_roadmap_id
    private PersonalRoadmap personalRoadmap;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    // 템플릿 단계(stage)를 개인화하여 오버라이드할 수 있음(선택)
    @Column(name = "stage", columnDefinition = "json")
    private String stage;

    @Column(name = "done_percent", nullable = false)
    private int donePercent; // 0~100

    @Column(name = "is_done", nullable = false)
    private boolean done;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==== 도메인 메서드 ====
    public void updateDonePercent(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("donePercent must be 0..100");
        }
        this.donePercent = percent;
        this.done = (percent == 100);
    }

    public void markDone() {
        this.donePercent = 100;
        this.done = true;
    }

    public void softDelete() {
        this.deleted = true;
    }
}
