package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingFile;


public interface SharingFileRepository extends JpaRepository<SharingFile, Integer> {

    SharingFile findFirstByShId(SharingBoard sharingBoard);

    boolean existsByShIdSharingId(Integer sharingId);

}
