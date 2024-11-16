package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.User;


public interface OldExamRepository extends JpaRepository<OldExam, Integer> {

    OldExam findFirstByAuthor(User user);

    OldExam findFirstByLectureId(LectureList lectureList);

}
