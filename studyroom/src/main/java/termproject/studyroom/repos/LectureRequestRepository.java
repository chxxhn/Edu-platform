package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.*;


public interface LectureRequestRepository extends JpaRepository<LectureRequest, Integer> {

    LectureRequest findFirstByLectureId(LectureList lectureList);

    Page<LectureRequest> findAll(Pageable pageable);

}
