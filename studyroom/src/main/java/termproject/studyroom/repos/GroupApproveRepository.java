package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import termproject.studyroom.domain.GroupApprove;

@Repository
public interface GroupApproveRepository extends JpaRepository<GroupApprove, Integer> {
}

