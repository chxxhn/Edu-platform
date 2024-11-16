package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.OldExamFile;


public interface OldExamFileRepository extends JpaRepository<OldExamFile, Integer> {

    OldExamFile findFirstByOeId(OldExam oldExam);

    boolean existsByOeIdOeId(Integer oeId);

}
