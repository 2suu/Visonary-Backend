package esu.visionary.domain.job;

<<<<<<< HEAD
import esu.visionary.domain.user.model.User2;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_recommendations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class JobRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_rec_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK → users.user_id
    private User2 user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false) // FK → jobs.job_id
    private Job job;

    // 0~100 범위를 가정, 소수 2자리까지
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    // 추가 정보(JSON): 예) {"reasons":["skill_match","interest_fit"],"weights":{"skill":0.7,"interest":0.3}}
    @Column(columnDefinition = "json")
    private String meta;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
=======
import esu.visionary.common.entity.BaseTimeEntity;
import esu.visionary.domain.user.model.User; // ✅ 여기로 변경
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_recommendation")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JobRecommendation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 연관관계 대상 타입을 User로
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // users.id FK
    private User user;

    // ... 나머지 필드들
>>>>>>> aff50e0 (최종 버전)
}
