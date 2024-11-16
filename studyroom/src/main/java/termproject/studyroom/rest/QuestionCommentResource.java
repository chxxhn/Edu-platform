package termproject.studyroom.rest;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.service.QuestionCommentService;


@RestController
@RequestMapping(value = "/api/questionComments", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionCommentResource {

    private final QuestionCommentService questionCommentService;

    public QuestionCommentResource(final QuestionCommentService questionCommentService) {
        this.questionCommentService = questionCommentService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionCommentDTO>> getAllQuestionComments() {
        return ResponseEntity.ok(questionCommentService.findAll());
    }

    @GetMapping("/{qcomId}")
    public ResponseEntity<QuestionCommentDTO> getQuestionComment(
            @PathVariable(name = "qcomId") final Integer qcomId) {
        return ResponseEntity.ok(questionCommentService.get(qcomId));
    }

    @PostMapping
    public ResponseEntity<Integer> createQuestionComment(
            @RequestBody @Valid final QuestionCommentDTO questionCommentDTO) {
        final Integer createdQcomId = questionCommentService.create(questionCommentDTO);
        return new ResponseEntity<>(createdQcomId, HttpStatus.CREATED);
    }

    @PutMapping("/{qcomId}")
    public ResponseEntity<Integer> updateQuestionComment(
            @PathVariable(name = "qcomId") final Integer qcomId,
            @RequestBody @Valid final QuestionCommentDTO questionCommentDTO) {
        questionCommentService.update(qcomId, questionCommentDTO);
        return ResponseEntity.ok(qcomId);
    }

    @DeleteMapping("/{qcomId}")
    public ResponseEntity<Void> deleteQuestionComment(
            @PathVariable(name = "qcomId") final Integer qcomId) {
        questionCommentService.delete(qcomId);
        return ResponseEntity.noContent().build();
    }

}
