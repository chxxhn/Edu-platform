package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingFile;
import termproject.studyroom.model.SharingFileDTO;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.SharingFileRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class SharingFileService {

    private final SharingFileRepository sharingFileRepository;
    private final SharingBoardRepository sharingBoardRepository;

    public SharingFileService(final SharingFileRepository sharingFileRepository,
            final SharingBoardRepository sharingBoardRepository) {
        this.sharingFileRepository = sharingFileRepository;
        this.sharingBoardRepository = sharingBoardRepository;
    }

    public List<SharingFileDTO> findAll() {
        final List<SharingFile> sharingFiles = sharingFileRepository.findAll(Sort.by("shfId"));
        return sharingFiles.stream()
                .map(sharingFile -> mapToDTO(sharingFile, new SharingFileDTO()))
                .toList();
    }

    public SharingFileDTO get(final Integer shfId) {
        return sharingFileRepository.findById(shfId)
                .map(sharingFile -> mapToDTO(sharingFile, new SharingFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SharingFileDTO sharingFileDTO) {
        final SharingFile sharingFile = new SharingFile();
        mapToEntity(sharingFileDTO, sharingFile);
        return sharingFileRepository.save(sharingFile).getShfId();
    }

    public void update(final Integer shfId, final SharingFileDTO sharingFileDTO) {
        final SharingFile sharingFile = sharingFileRepository.findById(shfId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sharingFileDTO, sharingFile);
        sharingFileRepository.save(sharingFile);
    }

    public void delete(final Integer shfId) {
        sharingFileRepository.deleteById(shfId);
    }

    private SharingFileDTO mapToDTO(final SharingFile sharingFile,
            final SharingFileDTO sharingFileDTO) {
        sharingFileDTO.setShfId(sharingFile.getShfId());
        sharingFileDTO.setSrc(sharingFile.getSrc());
        sharingFileDTO.setUuid(sharingFile.getUuid());
        sharingFileDTO.setFileName(sharingFile.getFileName());
        sharingFileDTO.setShId(sharingFile.getShId() == null ? null : sharingFile.getShId().getSharingId());
        return sharingFileDTO;
    }

    private SharingFile mapToEntity(final SharingFileDTO sharingFileDTO,
            final SharingFile sharingFile) {
        sharingFile.setSrc(sharingFileDTO.getSrc());
        sharingFile.setUuid(sharingFileDTO.getUuid());
        sharingFile.setFileName(sharingFileDTO.getFileName());
        final SharingBoard shId = sharingFileDTO.getShId() == null ? null : sharingBoardRepository.findById(sharingFileDTO.getShId())
                .orElseThrow(() -> new NotFoundException("shId not found"));
        sharingFile.setShId(shId);
        return sharingFile;
    }

    public boolean shIdExists(final Integer sharingId) {
        return sharingFileRepository.existsByShIdSharingId(sharingId);
    }

}
