package esu.visionary.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공통 생성/수정 시각.
 * - DB 컬럼명은 created_at / updated_at 로 고정
 * - 엔티티 쪽에서는 BaseTimeEntity 한 곳에서만 매핑(중복 매핑 금지)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /** 생성 시각: INSERT 시 자동 세팅, 이후 수정 불가 */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 수정 시각: INSERT/UPDATE 시 자동 갱신 */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
