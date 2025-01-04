package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureUser;
import termproject.studyroom.domain.LectureUserId;
import termproject.studyroom.domain.User;

import java.util.List;

@Repository
public interface LectureUserRepository extends JpaRepository<LectureUser, LectureUserId> {

    @Query("SELECT lu.lecture FROM LectureUser lu WHERE lu.user.id = :userId")
    List<LectureList> findLectureListsByUserId(@Param("userId") Integer userId);

    boolean existsByLecture_LectureIdAndUser_StdId(Integer lectureId, Integer stdId);


    @Query("SELECT l FROM LectureUser l WHERE l.lecture.lectureId = :lectureId")
    List<LectureUser> findByLectureId(@Param("lectureId") Integer lectureId);


    @Query("SELECT lu FROM LectureUser lu WHERE lu.lecture.lectureId = :lectureId AND lu.user.stdId NOT IN " +
            "(SELECT gu.user.stdId FROM GroupUser gu WHERE gu.lectureList.lectureId = :lectureId)")
    List<LectureUser> findAvailableLectureUsers(@Param("lectureId") Integer lectureId);

    @Query("SELECT COUNT(lu) > 0 FROM LectureUser lu " +
            "WHERE lu.lecture.lectureId = :lectureId " +
            "AND lu.user.stdId = :stdId " +
            "AND lu.user.stdId NOT IN " +
            "(SELECT gu.user.stdId FROM GroupUser gu WHERE gu.lectureList.lectureId = :lectureId)")
    boolean isUserAvailableForLecture(@Param("lectureId") Integer lectureId, @Param("stdId") Integer stdId);



}
