package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.QuestionCommentRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class QuestionCommentService {

    private final QuestionCommentRepository questionCommentRepository;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;

    public QuestionCommentService(final QuestionCommentRepository questionCommentRepository,
            final UserRepository userRepository,
            final QuestionBoardRepository questionBoardRepository) {
        this.questionCommentRepository = questionCommentRepository;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
    }

    public List<QuestionCommentDTO> findAll() {
        final List<QuestionComment> questionComments = questionCommentRepository.findAll(Sort.by("qcomId"));
        return questionComments.stream()
                .map(questionComment -> mapToDTO(questionComment, new QuestionCommentDTO()))
                .toList();
    }

    public QuestionCommentDTO get(final Integer qcomId) {
        return questionCommentRepository.findById(qcomId)
                .map(questionComment -> mapToDTO(questionComment, new QuestionCommentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final QuestionCommentDTO questionCommentDTO) {
        final QuestionComment questionComment = new QuestionComment();
        mapToEntity(questionCommentDTO, questionComment);
        return questionCommentRepository.save(questionComment).getQcomId();
    }

    public void update(final Integer qcomId, final QuestionCommentDTO questionCommentDTO) {
        final QuestionComment questionComment = questionCommentRepository.findById(qcomId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(questionCommentDTO, questionComment);
        questionCommentRepository.save(questionComment);
    }

    public void delete(final Integer qcomId) {
        questionCommentRepository.deleteById(qcomId);
    }

    private QuestionCommentDTO mapToDTO(final QuestionComment questionComment,
            final QuestionCommentDTO questionCommentDTO) {
        questionCommentDTO.setQcomId(questionComment.getQcomId());
        questionCommentDTO.setContent(questionComment.getContent());
        questionCommentDTO.setAuthor(questionComment.getAuthor() == null ? null : questionComment.getAuthor().getStdId());
        questionCommentDTO.setQId(questionComment.getQId() == null ? null : questionComment.getQId().getQuestionId());
        return questionCommentDTO;
    }

    private QuestionComment mapToEntity(final QuestionCommentDTO questionCommentDTO,
            final QuestionComment questionComment) {
        questionComment.setContent(questionCommentDTO.getContent());
        final User author = questionCommentDTO.getAuthor() == null ? null : userRepository.findById(questionCommentDTO.getAuthor())
                .orElseThrow(() -> new NotFoundException("author not found"));
        questionComment.setAuthor(author);
        final QuestionBoard qId = questionCommentDTO.getQId() == null ? null : questionBoardRepository.findById(questionCommentDTO.getQId())
                .orElseThrow(() -> new NotFoundException("qId not found"));
        questionComment.setQId(qId);
        return questionComment;
    }

}
