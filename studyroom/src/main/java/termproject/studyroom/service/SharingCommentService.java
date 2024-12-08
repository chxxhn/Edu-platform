package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.model.SharingCommentDTO;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.SharingCommentRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class SharingCommentService {

    private final SharingCommentRepository sharingCommentRepository;
    private final UserRepository userRepository;
    private final SharingBoardRepository sharingBoardRepository;

    public SharingCommentService(final SharingCommentRepository sharingCommentRepository,
            final UserRepository userRepository,
            final SharingBoardRepository sharingBoardRepository) {
        this.sharingCommentRepository = sharingCommentRepository;
        this.userRepository = userRepository;
        this.sharingBoardRepository = sharingBoardRepository;
    }

    public List<SharingCommentDTO> findAll() {
        final List<SharingComment> sharingComments = sharingCommentRepository.findAll(Sort.by("shcomId"));
        return sharingComments.stream()
                .map(sharingComment -> mapToDTO(sharingComment, new SharingCommentDTO()))
                .toList();
    }

    public SharingCommentDTO get(final Integer shcomId) {
        return sharingCommentRepository.findById(shcomId)
                .map(sharingComment -> mapToDTO(sharingComment, new SharingCommentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SharingCommentDTO sharingCommentDTO) {
        final SharingComment sharingComment = new SharingComment();
        mapToEntity(sharingCommentDTO, sharingComment);
        return sharingCommentRepository.save(sharingComment).getShcomId();
    }

    public void update(final Integer shcomId, final SharingCommentDTO sharingCommentDTO) {
        final SharingComment sharingComment = sharingCommentRepository.findById(shcomId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sharingCommentDTO, sharingComment);
        sharingCommentRepository.save(sharingComment);
    }

    public List<SharingCommentDTO> findBySharingId(Integer sharingId) {
        List<SharingComment> sharingComments = sharingCommentRepository.findByshIdSharingId(sharingId);
        return sharingComments.stream()
                .map(sharingComment -> mapToDTO(sharingComment, new SharingCommentDTO()))
                .toList();
    }

    public void delete(final Integer shcomId) {
        sharingCommentRepository.deleteById(shcomId);
    }

    private SharingCommentDTO mapToDTO(final SharingComment sharingComment,
            final SharingCommentDTO sharingCommentDTO) {
        sharingCommentDTO.setShcomId(sharingComment.getShcomId());
        sharingCommentDTO.setContent(sharingComment.getContent());
        sharingCommentDTO.setAuthor(sharingComment.getAuthor() == null ? null : sharingComment.getAuthor());
        sharingCommentDTO.setShId(sharingComment.getShId() == null ? null : sharingComment.getShId().getSharingId());
        return sharingCommentDTO;
    }

    private SharingComment mapToEntity(final SharingCommentDTO sharingCommentDTO,
            final SharingComment sharingComment) {
        sharingComment.setContent(sharingCommentDTO.getContent());
        final User author = sharingCommentDTO.getAuthor() == null ? null : userRepository.findById(sharingCommentDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("author not found"));
        sharingComment.setAuthor(author);
        final SharingBoard shId = sharingCommentDTO.getShId() == null ? null : sharingBoardRepository.findById(sharingCommentDTO.getShId())
                .orElseThrow(() -> new NotFoundException("shId not found"));
        sharingComment.setShId(shId);
        return sharingComment;
    }

}
