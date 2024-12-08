package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;

import java.util.Optional;


public interface LectureListRepository extends JpaRepository<LectureList, Integer> {

    LectureList findFirstByStdId(User user);

    boolean existsByNameIgnoreCase(String name);

    Optional<LectureList> findByLectureId(Integer lectureId);
    Optional<LectureList> findByName(String name); // 강의 이름으로 검색



}
