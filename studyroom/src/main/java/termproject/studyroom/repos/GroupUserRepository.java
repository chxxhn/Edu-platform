package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.GroupUser;
import termproject.studyroom.domain.GroupUserId;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {

    @Query("SELECT gu.team FROM GroupUser gu WHERE gu.groupUserId.userId = :userId AND gu.groupProject.gpId = :groupId")
    String findTeamByUserIdAndGroupId(@Param("userId") Integer userId, @Param("groupId") Integer groupId);


    @Query("SELECT CASE WHEN COUNT(gu) > 0 THEN true ELSE false END " +
            "FROM GroupUser gu " +
            "WHERE gu.groupUserId.userId = :userId AND gu.groupProject.gpId = :gpId")
    boolean existsByUserIdAndLectureId(@Param("userId") Integer userId, @Param("gpId") Integer gpId);

}
