package termproject.studyroom.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupProjectDTO;


public interface GroupProjectRepository extends JpaRepository<GroupProject, Integer> {

    GroupProject findFirstByCreatedUserId(User user);

    GroupProject findFirstByLectureId(LectureList lectureList);

    boolean existsByGroupNameIgnoreCase(String groupName);

    @Query("SELECT q FROM GroupProject q WHERE q.lectureId = :lecture")
    Page<GroupProject> findByLectureIdWithPaging(LectureList lecture, PageRequest of);

    @Query(value = "SELECT * FROM group_projects gp WHERE gp.lecture_id_id = :lectureId AND gp.group_valid IS NOT DISTINCT FROM :groupValid",
            countQuery = "SELECT COUNT(*) FROM group_projects gp WHERE gp.lecture_id_id = :lectureId AND gp.group_valid IS NOT DISTINCT FROM :groupValid",
            nativeQuery = true)
    Page<GroupProject> findByLectureIdAndGroupValidWithPaging(
            @Param("lectureId") Integer lectureId,
            @Param("groupValid") Boolean groupValid,
            Pageable pageable);

}
