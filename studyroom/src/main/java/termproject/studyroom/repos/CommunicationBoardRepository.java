package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.CommunicationBoard;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


public interface CommunicationBoardRepository extends JpaRepository<CommunicationBoard, Integer> {

    CommunicationBoard findFirstByAuthor(User user);

    CommunicationBoard findFirstByLectureId(LectureList lectureList);

}
