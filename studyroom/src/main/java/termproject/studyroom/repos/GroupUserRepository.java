package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.GroupUser;
import termproject.studyroom.domain.GroupUserId;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {
}
