package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.GroupUser;
import termproject.studyroom.domain.GroupUserId;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {

    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END " +
            "FROM GroupUser gu " +
            "WHERE gu.groupUserId.userId = :userId AND gu.groupUserId.lectureId = :lectureId")
    boolean existsByUserIdAndLectureId(@Param("userId") Integer userId, @Param("gpId") Integer lectureId);

}
