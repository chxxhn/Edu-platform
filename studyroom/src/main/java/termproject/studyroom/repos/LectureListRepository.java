package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


public interface LectureListRepository extends JpaRepository<LectureList, Integer> {

    LectureList findFirstByStdId(User user);

    boolean existsByNameIgnoreCase(String name);

}
