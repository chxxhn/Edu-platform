package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


public interface GroupProjectRepository extends JpaRepository<GroupProject, Integer> {

    GroupProject findFirstByCreatedUserId(User user);

    GroupProject findFirstByLectureId(LectureList lectureList);

    boolean existsByGroupNameIgnoreCase(String groupName);

}
