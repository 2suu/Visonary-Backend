package esu.visionary.domain.survey;

import esu.visionary.domain.survey.enums.SurveyStatus;
import esu.visionary.domain.user.model.User2;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SurveySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)   // FK → users.user_id
    private User2 user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_form_id", nullable = false)  // FK → survey_forms.survey_form_id
    private SurveyForm form;

    // 설문 세션 당시 폼 버전(폼 변경 대비 스냅샷용)
    @Column(nullable = false, length = 50)
    private String version;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyStatus status; // IN_PROGRESS/COMPLETED/CANCELLED

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // === 도메인 동작 예시 ===
    public void start(LocalDateTime now) {
        this.startedAt = now;
        this.status = SurveyStatus.IN_PROGRESS;
    }

    public void complete(LocalDateTime now) {
        this.completedAt = now;
        this.status = SurveyStatus.COMPLETED;
    }

    public void cancel() {
        this.status = SurveyStatus.CANCELLED;
    }
}
