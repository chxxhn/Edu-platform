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
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.service.QuestionBoardService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/questionBoards", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuestionBoardResource {

    private final QuestionBoardService questionBoardService;

    public QuestionBoardResource(final QuestionBoardService questionBoardService) {
        this.questionBoardService = questionBoardService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionBoardDTO>> getAllQuestionBoards() {
        return ResponseEntity.ok(questionBoardService.findAll());
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionBoardDTO> getQuestionBoard(
            @PathVariable(name = "questionId") final Integer questionId) {
        return ResponseEntity.ok(questionBoardService.get(questionId));
    }

    @PostMapping
    public ResponseEntity<Integer> createQuestionBoard(
            @RequestBody @Valid final QuestionBoardDTO questionBoardDTO) {
        final Integer createdQuestionId = questionBoardService.create(questionBoardDTO);
        return new ResponseEntity<>(createdQuestionId, HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Integer> updateQuestionBoard(
            @PathVariable(name = "questionId") final Integer questionId,
            @RequestBody @Valid final QuestionBoardDTO questionBoardDTO) {
        questionBoardService.update(questionId, questionBoardDTO);
        return ResponseEntity.ok(questionId);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestionBoard(
            @PathVariable(name = "questionId") final Integer questionId) {
        final ReferencedWarning referencedWarning = questionBoardService.getReferencedWarning(questionId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        questionBoardService.delete(questionId);
        return ResponseEntity.noContent().build();
    }

}
