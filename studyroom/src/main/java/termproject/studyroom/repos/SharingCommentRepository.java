package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingComment;
import termproject.studyroom.domain.User;


public interface SharingCommentRepository extends JpaRepository<SharingComment, Integer> {

    SharingComment findFirstByAuthor(User user);

    SharingComment findFirstByShId(SharingBoard sharingBoard);

}
