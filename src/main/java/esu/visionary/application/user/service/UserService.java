package esu.visionary.application.user.service;

import esu.visionary.domain.user.model.User;
import esu.visionary.infrastructure.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /** 로그인 아이디(문자열) 중복 여부 확인 */
    public boolean existsById(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /** 닉네임 중복 여부 확인 */
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickName(nickname);
    }

    /** 4자리 사용자ID(userId) 중복 여부 확인 */
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    /** 로그인 아이디로 User 조회 */
    public User getUserById(String loginId) {
        return userRepository.findByLoginId(loginId).orElse(null);
    }

    /** 신규/갱신 저장 (id==null → INSERT, id!=null → UPDATE) */
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
