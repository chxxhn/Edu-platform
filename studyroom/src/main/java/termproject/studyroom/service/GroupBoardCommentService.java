package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupBoardCommentDTO;
import termproject.studyroom.repos.GroupBoardCommentRepository;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class GroupBoardCommentService {

    private final GroupBoardCommentRepository groupBoardCommentRepository;
    private final UserRepository userRepository;
    private final GroupBoardRepository groupBoardRepository;

    public GroupBoardCommentService(final GroupBoardCommentRepository groupBoardCommentRepository,
            final UserRepository userRepository, final GroupBoardRepository groupBoardRepository) {
        this.groupBoardCommentRepository = groupBoardCommentRepository;
        this.userRepository = userRepository;
        this.groupBoardRepository = groupBoardRepository;
    }

    public List<GroupBoardCommentDTO> findAll() {
        final List<GroupBoardComment> groupBoardComments = groupBoardCommentRepository.findAll(Sort.by("gbcomId"));
        return groupBoardComments.stream()
                .map(groupBoardComment -> mapToDTO(groupBoardComment, new GroupBoardCommentDTO()))
                .toList();
    }

    public GroupBoardCommentDTO get(final Integer gbcomId) {
        return groupBoardCommentRepository.findById(gbcomId)
                .map(groupBoardComment -> mapToDTO(groupBoardComment, new GroupBoardCommentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GroupBoardCommentDTO groupBoardCommentDTO) {
        final GroupBoardComment groupBoardComment = new GroupBoardComment();
        mapToEntity(groupBoardCommentDTO, groupBoardComment);
        return groupBoardCommentRepository.save(groupBoardComment).getGbcomId();
    }

    public void update(final Integer gbcomId, final GroupBoardCommentDTO groupBoardCommentDTO) {
        final GroupBoardComment groupBoardComment = groupBoardCommentRepository.findById(gbcomId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(groupBoardCommentDTO, groupBoardComment);
        groupBoardCommentRepository.save(groupBoardComment);
    }

    public void delete(final Integer gbcomId) {
        groupBoardCommentRepository.deleteById(gbcomId);
    }

    private GroupBoardCommentDTO mapToDTO(final GroupBoardComment groupBoardComment,
            final GroupBoardCommentDTO groupBoardCommentDTO) {
        groupBoardCommentDTO.setGbcomId(groupBoardComment.getGbcomId());
        groupBoardCommentDTO.setContent(groupBoardComment.getContent());
        groupBoardCommentDTO.setAuthor(groupBoardComment.getAuthor() == null ? null : groupBoardComment.getAuthor().getStdId());
        groupBoardCommentDTO.setGbId(groupBoardComment.getGbId() == null ? null : groupBoardComment.getGbId().getGbId());
        return groupBoardCommentDTO;
    }

    private GroupBoardComment mapToEntity(final GroupBoardCommentDTO groupBoardCommentDTO,
            final GroupBoardComment groupBoardComment) {
        groupBoardComment.setContent(groupBoardCommentDTO.getContent());
        final User author = groupBoardCommentDTO.getAuthor() == null ? null : userRepository.findById(groupBoardCommentDTO.getAuthor())
                .orElseThrow(() -> new NotFoundException("author not found"));
        groupBoardComment.setAuthor(author);
        final GroupBoard gbId = groupBoardCommentDTO.getGbId() == null ? null : groupBoardRepository.findById(groupBoardCommentDTO.getGbId())
                .orElseThrow(() -> new NotFoundException("gbId not found"));
        groupBoardComment.setGbId(gbId);
        return groupBoardComment;
    }

}
