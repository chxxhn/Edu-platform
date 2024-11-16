package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardComment;
import termproject.studyroom.domain.User;


public interface GroupBoardCommentRepository extends JpaRepository<GroupBoardComment, Integer> {

    GroupBoardComment findFirstByAuthor(User user);

    GroupBoardComment findFirstByGbId(GroupBoard groupBoard);

}
