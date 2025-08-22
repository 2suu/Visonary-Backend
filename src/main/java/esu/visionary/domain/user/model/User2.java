package esu.visionary.domain.user.model;

<<<<<<< HEAD
import esu.visionary.domain.user.enums.AuthProvider;
import esu.visionary.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
=======
import esu.visionary.common.entity.BaseTimeEntity;
import esu.visionary.domain.user.enums.AuthProvider;
import esu.visionary.domain.user.enums.UserStatus;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

/**
 * 옵션1: 매핑 제외
 * - 더 이상 어떤 테이블에도 매핑되지 않도록 @Entity/@Table 제거
 * - @MappedSuperclass 로 변경 (필드 정의만 공유; 현재는 상속받는 엔티티 없으므로 DB에 영향 없음)
 * - 필요 시 나중에 별도 테이블로 분리해 엔티티화할 수 있음
 */
@MappedSuperclass
>>>>>>> aff50e0 (최종 버전)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
<<<<<<< HEAD
@EntityListeners(AuditingEntityListener.class)
public class User2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;   // PK

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;   // LOCAL/GOOGLE/KAKAO/NAVER

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;   // ACTIVE/INACTIVE/DELETED

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ==== 도메인 메서드 예시 ====
=======
public class User2 extends BaseTimeEntity {

    // JPA 매핑 제거: 필드에 @Id/@Column 등 붙이지 않음 (순수 POJO)
    protected Long id;                 // 과거 PK (현재 미사용)
    protected String email;
    protected String passwordHash;
    protected String nickname;
    protected AuthProvider provider;   // LOCAL/GOOGLE/KAKAO/NAVER
    protected String providerId;
    protected UserStatus status;       // ACTIVE/INACTIVE/DELETED

    // ==== 도메인 메서드 (그대로 사용 가능) ====
>>>>>>> aff50e0 (최종 버전)
    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }
}
