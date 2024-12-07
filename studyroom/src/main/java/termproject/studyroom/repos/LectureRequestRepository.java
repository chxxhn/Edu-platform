package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.User;


public interface LectureRequestRepository extends JpaRepository<LectureRequest, Integer> {

    LectureRequest findFirstByLectureId(LectureList lectureList);

}
