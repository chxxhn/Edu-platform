package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingComment;
import termproject.studyroom.domain.User;

import java.util.List;


public interface SharingCommentRepository extends JpaRepository<SharingComment, Integer> {

    SharingComment findFirstByAuthor(User user);

    SharingComment findFirstByShId(SharingBoard sharingBoard);

    List<SharingComment> findByshIdSharingId(Integer sharingId);

}
