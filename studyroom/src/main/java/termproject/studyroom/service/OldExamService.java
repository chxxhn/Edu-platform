package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.OldExamFile;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.OldExamDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.OldExamFileRepository;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class OldExamService {

    private final OldExamRepository oldExamRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final OldExamFileRepository oldExamFileRepository;

    public OldExamService(final OldExamRepository oldExamRepository,
            final UserRepository userRepository, final LectureListRepository lectureListRepository,
            final OldExamFileRepository oldExamFileRepository) {
        this.oldExamRepository = oldExamRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.oldExamFileRepository = oldExamFileRepository;
    }

    public List<OldExamDTO> findAll() {
        final List<OldExam> oldExams = oldExamRepository.findAll(Sort.by("oeId"));
        return oldExams.stream()
                .map(oldExam -> mapToDTO(oldExam, new OldExamDTO()))
                .toList();
    }

    public OldExamDTO get(final Integer oeId) {
        return oldExamRepository.findById(oeId)
                .map(oldExam -> mapToDTO(oldExam, new OldExamDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final OldExamDTO oldExamDTO) {
        final OldExam oldExam = new OldExam();
        mapToEntity(oldExamDTO, oldExam);
        return oldExamRepository.save(oldExam).getOeId();
    }

    public void update(final Integer oeId, final OldExamDTO oldExamDTO) {
        final OldExam oldExam = oldExamRepository.findById(oeId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(oldExamDTO, oldExam);
        oldExamRepository.save(oldExam);
    }

    public void delete(final Integer oeId) {
        oldExamRepository.deleteById(oeId);
    }

    private OldExamDTO mapToDTO(final OldExam oldExam, final OldExamDTO oldExamDTO) {
        oldExamDTO.setOeId(oldExam.getOeId());
        oldExamDTO.setTitle(oldExam.getTitle());
        oldExamDTO.setContent(oldExam.getContent());
        oldExamDTO.setAuthor(oldExam.getAuthor() == null ? null : oldExam.getAuthor());
        oldExamDTO.setLectureId(oldExam.getLectureId() == null ? null : oldExam.getLectureId());
        return oldExamDTO;
    }

    private OldExam mapToEntity(final OldExamDTO oldExamDTO, final OldExam oldExam) {
        oldExam.setTitle(oldExamDTO.getTitle());
        oldExam.setContent(oldExamDTO.getContent());
        final User author = oldExamDTO.getAuthor() == null ? null : userRepository.findById(oldExamDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("author not found"));
        oldExam.setAuthor(author);
        final LectureList lectureId = oldExamDTO.getLectureId() == null ? null : lectureListRepository.findById(oldExamDTO.getLectureId().getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        oldExam.setLectureId(lectureId);
        return oldExam;
    }

    public ReferencedWarning getReferencedWarning(final Integer oeId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final OldExam oldExam = oldExamRepository.findById(oeId)
                .orElseThrow(NotFoundException::new);
        final OldExamFile oeIdOldExamFile = oldExamFileRepository.findFirstByOeId(oldExam);
        if (oeIdOldExamFile != null) {
            referencedWarning.setKey("oldExam.oldExamFile.oeId.referenced");
            referencedWarning.addParam(oeIdOldExamFile.getOefId());
            return referencedWarning;
        }
        return null;
    }

}
