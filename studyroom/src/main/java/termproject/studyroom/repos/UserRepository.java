package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.User;


public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByEmailIgnoreCase(String email);

}
