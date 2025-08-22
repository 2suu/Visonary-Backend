package esu.visionary.infrastructure.user.repository;

import esu.visionary.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 아이디 중복 확인
    boolean existsByLoginId(String loginId);

    // 닉네임 중복 확인
    boolean existsByNickName(String nickName);

    // 4자리 사용자ID(기존 userCode4) 중복 확인
    boolean existsByUserId(String userId);

    // 로그인 아이디로 사용자 조회
    Optional<User> findByLoginId(String loginId);
}
