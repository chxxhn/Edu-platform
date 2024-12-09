package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.Like;
import termproject.studyroom.domain.User;
import termproject.studyroom.domain.Warn;
import termproject.studyroom.model.BoardType;

import java.util.List;
import java.util.Optional;

public interface WarnRepository extends JpaRepository<Warn, Integer> {
    Warn findFirstByUser(User user);

    Warn findByUserAndPostIdAndBoardType(User user, Integer postId, BoardType boardType);

    int countByPostId(Integer postId);

    List<Warn> findByPostIdAndBoardType(Integer postId, BoardType boardType);

}