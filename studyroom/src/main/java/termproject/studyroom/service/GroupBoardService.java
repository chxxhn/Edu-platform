package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardComment;
import termproject.studyroom.domain.GroupBoardFile;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupBoardDTO;
import termproject.studyroom.repos.GroupBoardCommentRepository;
import termproject.studyroom.repos.GroupBoardFileRepository;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.repos.GroupProjectRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class GroupBoardService {

    private final GroupBoardRepository groupBoardRepository;
    private final UserRepository userRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final GroupBoardCommentRepository groupBoardCommentRepository;
    private final GroupBoardFileRepository groupBoardFileRepository;

    public GroupBoardService(final GroupBoardRepository groupBoardRepository,
            final UserRepository userRepository,
            final GroupProjectRepository groupProjectRepository,
            final GroupBoardCommentRepository groupBoardCommentRepository,
            final GroupBoardFileRepository groupBoardFileRepository) {
        this.groupBoardRepository = groupBoardRepository;
        this.userRepository = userRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.groupBoardCommentRepository = groupBoardCommentRepository;
        this.groupBoardFileRepository = groupBoardFileRepository;
    }

    public List<GroupBoardDTO> findAll() {
        final List<GroupBoard> groupBoards = groupBoardRepository.findAll(Sort.by("gbId"));
        return groupBoards.stream()
                .map(groupBoard -> mapToDTO(groupBoard, new GroupBoardDTO()))
                .toList();
    }

    public GroupBoardDTO get(final Integer gbId) {
        return groupBoardRepository.findById(gbId)
                .map(groupBoard -> mapToDTO(groupBoard, new GroupBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GroupBoardDTO groupBoardDTO) {
        final GroupBoard groupBoard = new GroupBoard();
        mapToEntity(groupBoardDTO, groupBoard);
        return groupBoardRepository.save(groupBoard).getGbId();
    }

    public void update(final Integer gbId, final GroupBoardDTO groupBoardDTO) {
        final GroupBoard groupBoard = groupBoardRepository.findById(gbId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(groupBoardDTO, groupBoard);
        groupBoardRepository.save(groupBoard);
    }

    public void delete(final Integer gbId) {
        groupBoardRepository.deleteById(gbId);
    }

    private GroupBoardDTO mapToDTO(final GroupBoard groupBoard, final GroupBoardDTO groupBoardDTO) {
        groupBoardDTO.setGbId(groupBoard.getGbId());
        groupBoardDTO.setTitle(groupBoard.getTitle());
        groupBoardDTO.setContent(groupBoard.getContent());
        groupBoardDTO.setAuthor(groupBoard.getAuthor() == null ? null : groupBoard.getAuthor().getStdId());
        groupBoardDTO.setGpId(groupBoard.getGpId() == null ? null : groupBoard.getGpId().getGpId());
        return groupBoardDTO;
    }

    private GroupBoard mapToEntity(final GroupBoardDTO groupBoardDTO, final GroupBoard groupBoard) {
        groupBoard.setTitle(groupBoardDTO.getTitle());
        groupBoard.setContent(groupBoardDTO.getContent());
        final User author = groupBoardDTO.getAuthor() == null ? null : userRepository.findById(groupBoardDTO.getAuthor())
                .orElseThrow(() -> new NotFoundException("author not found"));
        groupBoard.setAuthor(author);
        final GroupProject gpId = groupBoardDTO.getGpId() == null ? null : groupProjectRepository.findById(groupBoardDTO.getGpId())
                .orElseThrow(() -> new NotFoundException("gpId not found"));
        groupBoard.setGpId(gpId);
        return groupBoard;
    }

    public ReferencedWarning getReferencedWarning(final Integer gbId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final GroupBoard groupBoard = groupBoardRepository.findById(gbId)
                .orElseThrow(NotFoundException::new);
        final GroupBoardComment gbIdGroupBoardComment = groupBoardCommentRepository.findFirstByGbId(groupBoard);
        if (gbIdGroupBoardComment != null) {
            referencedWarning.setKey("groupBoard.groupBoardComment.gbId.referenced");
            referencedWarning.addParam(gbIdGroupBoardComment.getGbcomId());
            return referencedWarning;
        }
        final GroupBoardFile gbIdGroupBoardFile = groupBoardFileRepository.findFirstByGbId(groupBoard);
        if (gbIdGroupBoardFile != null) {
            referencedWarning.setKey("groupBoard.groupBoardFile.gbId.referenced");
            referencedWarning.addParam(gbIdGroupBoardFile.getGbfId());
            return referencedWarning;
        }
        return null;
    }

}
