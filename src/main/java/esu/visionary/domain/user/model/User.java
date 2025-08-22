package esu.visionary.domain.user.model;

import esu.visionary.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_login_id", columnList = "loginId", unique = true),
                @Index(name = "idx_users_nickname", columnList = "nickName"),
                @Index(name = "idx_users_user_id_4", columnList = "userId", unique = true) // ← 변경
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseTimeEntity {

    /** 내부 PK — DB가 생성 (API 응답에 노출 안 함) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인용 아이디(문자열) — 프론트의 request.id */
    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    /** 4자리 사용자 코드 — 비즈니스용 사용자ID (중복 금지) */
    @Column(name = "userId", length = 4, unique = true)
    private String userId;  // ← userCode4 → userId로 변경

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;

    /** 낙관적 락 */
    @Version
    private Long version;
}
