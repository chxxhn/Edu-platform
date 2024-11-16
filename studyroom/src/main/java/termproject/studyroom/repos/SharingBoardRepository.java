package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.User;


public interface SharingBoardRepository extends JpaRepository<SharingBoard, Integer> {

    SharingBoard findFirstByUserId(User user);

    SharingBoard findFirstByLectureId(LectureList lectureList);

}
