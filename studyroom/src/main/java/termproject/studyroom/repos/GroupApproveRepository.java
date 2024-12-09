package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.GroupApprove;
import termproject.studyroom.domain.LectureList;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupApproveRepository extends JpaRepository<GroupApprove, Integer> {
    List<GroupApprove> findByLectureList(LectureList lectureList);

    Optional<GroupApprove> findByLectureList_LectureIdAndGroupProject_GpId(Integer lectureId, Integer gpId);

}

