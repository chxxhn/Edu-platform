package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import termproject.studyroom.domain.*;


public interface SharingBoardRepository extends JpaRepository<SharingBoard, Integer> {

    SharingBoard findFirstByUserId(User user);

    SharingBoard findFirstByLectureId(LectureList lectureList);

    Page<SharingBoard> findAll(Pageable pageable);

    @Query("SELECT s FROM SharingBoard s WHERE s.lectureId = :lectureId")
    Page<SharingBoard> findByLectureIdWithPaging(@Param("lectureId") LectureList lectureId, Pageable pageable);}

