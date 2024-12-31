package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.Alarm;
import termproject.studyroom.domain.User;

import java.util.List;


public interface AlarmRepository extends JpaRepository<Alarm, Integer> {

    Alarm findFirstByUserId(User user);
    List<Alarm> findByUserId(User user);

}
