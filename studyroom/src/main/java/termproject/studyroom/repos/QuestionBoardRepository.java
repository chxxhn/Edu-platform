package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;

import java.util.List;


public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, Integer> {

    QuestionBoard findFirstByAuthor(User user);

    QuestionBoard findFirstByLectureId(LectureList lectureList);

    Page<QuestionBoard> findAll(Pageable pageable);
    List<QuestionBoard> findByLectureId(LectureList lectureId); // 특정 강의의 질문 목록 조회


    @Query("SELECT q FROM QuestionBoard q WHERE q.lectureId = :lectureId")
    Page<QuestionBoard> findByLectureIdWithPaging(@Param("lectureId") LectureList lectureId, Pageable pageable);}
