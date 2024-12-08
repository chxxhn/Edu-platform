package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;


public interface OldExamRepository extends JpaRepository<OldExam, Integer> {

    OldExam findFirstByAuthor(User user);

    OldExam findFirstByLectureId(LectureList lectureList);

    Page<OldExam> findAll(Pageable pageable);

    @Query("SELECT q FROM OldExam q WHERE q.lectureId = :lectureId")
    Page<OldExam> findByLectureIdWithPaging(@Param("lectureId") LectureList lectureId, Pageable pageable);}

