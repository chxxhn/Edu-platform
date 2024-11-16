package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.OldExamFile;
import termproject.studyroom.model.OldExamFileDTO;
import termproject.studyroom.repos.OldExamFileRepository;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class OldExamFileService {

    private final OldExamFileRepository oldExamFileRepository;
    private final OldExamRepository oldExamRepository;

    public OldExamFileService(final OldExamFileRepository oldExamFileRepository,
            final OldExamRepository oldExamRepository) {
        this.oldExamFileRepository = oldExamFileRepository;
        this.oldExamRepository = oldExamRepository;
    }

    public List<OldExamFileDTO> findAll() {
        final List<OldExamFile> oldExamFiles = oldExamFileRepository.findAll(Sort.by("oefId"));
        return oldExamFiles.stream()
                .map(oldExamFile -> mapToDTO(oldExamFile, new OldExamFileDTO()))
                .toList();
    }

    public OldExamFileDTO get(final Integer oefId) {
        return oldExamFileRepository.findById(oefId)
                .map(oldExamFile -> mapToDTO(oldExamFile, new OldExamFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final OldExamFileDTO oldExamFileDTO) {
        final OldExamFile oldExamFile = new OldExamFile();
        mapToEntity(oldExamFileDTO, oldExamFile);
        return oldExamFileRepository.save(oldExamFile).getOefId();
    }

    public void update(final Integer oefId, final OldExamFileDTO oldExamFileDTO) {
        final OldExamFile oldExamFile = oldExamFileRepository.findById(oefId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(oldExamFileDTO, oldExamFile);
        oldExamFileRepository.save(oldExamFile);
    }

    public void delete(final Integer oefId) {
        oldExamFileRepository.deleteById(oefId);
    }

    private OldExamFileDTO mapToDTO(final OldExamFile oldExamFile,
            final OldExamFileDTO oldExamFileDTO) {
        oldExamFileDTO.setOefId(oldExamFile.getOefId());
        oldExamFileDTO.setUuid(oldExamFile.getUuid());
        oldExamFileDTO.setSrc(oldExamFile.getSrc());
        oldExamFileDTO.setFileName(oldExamFile.getFileName());
        oldExamFileDTO.setOeId(oldExamFile.getOeId() == null ? null : oldExamFile.getOeId().getOeId());
        return oldExamFileDTO;
    }

    private OldExamFile mapToEntity(final OldExamFileDTO oldExamFileDTO,
            final OldExamFile oldExamFile) {
        oldExamFile.setUuid(oldExamFileDTO.getUuid());
        oldExamFile.setSrc(oldExamFileDTO.getSrc());
        oldExamFile.setFileName(oldExamFileDTO.getFileName());
        final OldExam oeId = oldExamFileDTO.getOeId() == null ? null : oldExamRepository.findById(oldExamFileDTO.getOeId())
                .orElseThrow(() -> new NotFoundException("oeId not found"));
        oldExamFile.setOeId(oeId);
        return oldExamFile;
    }

    public boolean oeIdExists(final Integer oeId) {
        return oldExamFileRepository.existsByOeIdOeId(oeId);
    }

}
