package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.User;

import java.util.List;


public interface GroupBoardRepository extends JpaRepository<GroupBoard, Integer> {

    GroupBoard findFirstByAuthor(User user);

    GroupBoard findFirstByGpId(GroupProject groupProject);

    List<GroupBoard> findByGpId_GpId(Integer gpId);

}
