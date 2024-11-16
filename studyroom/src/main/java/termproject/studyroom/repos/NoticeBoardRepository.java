package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.User;


public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Integer> {

    NoticeBoard findFirstByAuthor(User user);

    NoticeBoard findFirstByLectureId(LectureList lectureList);

}
