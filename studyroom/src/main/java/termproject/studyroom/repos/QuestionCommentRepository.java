package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.User;

import java.util.List;


public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Integer> {

    QuestionComment findFirstByAuthor(User user);

    QuestionComment findFirstByqId(QuestionBoard questionBoard);

    List<QuestionComment> findByqIdQuestionId(Integer questionId);

}
