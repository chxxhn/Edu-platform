package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardFile;


public interface GroupBoardFileRepository extends JpaRepository<GroupBoardFile, Integer> {

    GroupBoardFile findFirstByGbId(GroupBoard groupBoard);

    boolean existsByGbIdGbId(Integer gbId);

}
