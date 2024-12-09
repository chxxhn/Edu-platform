package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.Like;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.BoardType;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findFirstByUser(User user);

    Like findByUserAndPostIdAndBoardType(User user, Integer postId, BoardType boardType);

    int countByPostId(Integer postId);

    List<Like> findByPostIdAndBoardType(Integer postId, BoardType boardType);

}