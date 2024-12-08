package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.Grade;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByEmailIgnoreCase(String email);

    // 이메일로 사용자 조회
    Optional<User> findByEmailIgnoreCase(String email);

    // 닉네임으로 사용자 조회
    Optional<User> findByNicknameIgnoreCase(String nickname);

    Optional<User> findByEmail(String email);

    Page<User> findByGrade(Grade grade, Pageable pageable);
}
