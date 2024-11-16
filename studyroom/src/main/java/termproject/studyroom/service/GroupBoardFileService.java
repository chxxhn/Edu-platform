package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardFile;
import termproject.studyroom.model.GroupBoardFileDTO;
import termproject.studyroom.repos.GroupBoardFileRepository;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class GroupBoardFileService {

    private final GroupBoardFileRepository groupBoardFileRepository;
    private final GroupBoardRepository groupBoardRepository;

    public GroupBoardFileService(final GroupBoardFileRepository groupBoardFileRepository,
            final GroupBoardRepository groupBoardRepository) {
        this.groupBoardFileRepository = groupBoardFileRepository;
        this.groupBoardRepository = groupBoardRepository;
    }

    public List<GroupBoardFileDTO> findAll() {
        final List<GroupBoardFile> groupBoardFiles = groupBoardFileRepository.findAll(Sort.by("gbfId"));
        return groupBoardFiles.stream()
                .map(groupBoardFile -> mapToDTO(groupBoardFile, new GroupBoardFileDTO()))
                .toList();
    }

    public GroupBoardFileDTO get(final Integer gbfId) {
        return groupBoardFileRepository.findById(gbfId)
                .map(groupBoardFile -> mapToDTO(groupBoardFile, new GroupBoardFileDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GroupBoardFileDTO groupBoardFileDTO) {
        final GroupBoardFile groupBoardFile = new GroupBoardFile();
        mapToEntity(groupBoardFileDTO, groupBoardFile);
        return groupBoardFileRepository.save(groupBoardFile).getGbfId();
    }

    public void update(final Integer gbfId, final GroupBoardFileDTO groupBoardFileDTO) {
        final GroupBoardFile groupBoardFile = groupBoardFileRepository.findById(gbfId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(groupBoardFileDTO, groupBoardFile);
        groupBoardFileRepository.save(groupBoardFile);
    }

    public void delete(final Integer gbfId) {
        groupBoardFileRepository.deleteById(gbfId);
    }

    private GroupBoardFileDTO mapToDTO(final GroupBoardFile groupBoardFile,
            final GroupBoardFileDTO groupBoardFileDTO) {
        groupBoardFileDTO.setGbfId(groupBoardFile.getGbfId());
        groupBoardFileDTO.setSrc(groupBoardFile.getSrc());
        groupBoardFileDTO.setUuid(groupBoardFile.getUuid());
        groupBoardFileDTO.setFileName(groupBoardFile.getFileName());
        groupBoardFileDTO.setGbId(groupBoardFile.getGbId() == null ? null : groupBoardFile.getGbId().getGbId());
        return groupBoardFileDTO;
    }

    private GroupBoardFile mapToEntity(final GroupBoardFileDTO groupBoardFileDTO,
            final GroupBoardFile groupBoardFile) {
        groupBoardFile.setSrc(groupBoardFileDTO.getSrc());
        groupBoardFile.setUuid(groupBoardFileDTO.getUuid());
        groupBoardFile.setFileName(groupBoardFileDTO.getFileName());
        final GroupBoard gbId = groupBoardFileDTO.getGbId() == null ? null : groupBoardRepository.findById(groupBoardFileDTO.getGbId())
                .orElseThrow(() -> new NotFoundException("gbId not found"));
        groupBoardFile.setGbId(gbId);
        return groupBoardFile;
    }

    public boolean gbIdExists(final Integer gbId) {
        return groupBoardFileRepository.existsByGbIdGbId(gbId);
    }

}
