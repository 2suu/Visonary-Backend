package esu.visionary.infrastructure.user.repository;

import esu.visionary.domain.user.model.User2;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User2, Long> {
    // 필요하면 여기서 findByEmail 같은 쿼리 메서드 선언 가능
    // Optional<User2> findByEmail(String email);
}
