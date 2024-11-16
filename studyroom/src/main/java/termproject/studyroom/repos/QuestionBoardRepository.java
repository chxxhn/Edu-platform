package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;


public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Integer> {

    QuestionBoard findFirstByAuthor(User user);

    QuestionBoard findFirstByLectureId(LectureList lectureList);

}
