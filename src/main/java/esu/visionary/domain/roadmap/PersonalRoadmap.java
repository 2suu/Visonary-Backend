package esu.visionary.domain.roadmap;

<<<<<<< HEAD
import esu.visionary.domain.user.model.User2;
=======
import esu.visionary.domain.user.model.User;
>>>>>>> aff50e0 (최종 버전)
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_roadmaps")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PersonalRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_roadmap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK → users.user_id
<<<<<<< HEAD
    private User2 user;
=======
    private User user;
>>>>>>> aff50e0 (최종 버전)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_template_id", nullable = false) // FK → roadmap_templates.roadmap_template_id
    private RoadmapTemplate template;

    @Column(name = "title_custom", length = 200)
    private String titleCustom;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "progress_percent", nullable = false)
    private int progressPercent; // 0~100

    @Column(name = "is_personalized", nullable = false)
    private boolean personalized;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==== 도메인 메서드 ====
    public void rename(String newTitle) {
        this.titleCustom = newTitle;
    }

    public void updateProgress(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("progressPercent must be 0..100");
        }
        this.progressPercent = percent;
    }

    public void markPersonalized() {
        this.personalized = true;
    }
}
