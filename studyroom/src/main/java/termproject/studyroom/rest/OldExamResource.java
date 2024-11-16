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
import termproject.studyroom.model.OldExamDTO;
import termproject.studyroom.service.OldExamService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/oldExams", produces = MediaType.APPLICATION_JSON_VALUE)
public class OldExamResource {

    private final OldExamService oldExamService;

    public OldExamResource(final OldExamService oldExamService) {
        this.oldExamService = oldExamService;
    }

    @GetMapping
    public ResponseEntity<List<OldExamDTO>> getAllOldExams() {
        return ResponseEntity.ok(oldExamService.findAll());
    }

    @GetMapping("/{oeId}")
    public ResponseEntity<OldExamDTO> getOldExam(@PathVariable(name = "oeId") final Integer oeId) {
        return ResponseEntity.ok(oldExamService.get(oeId));
    }

    @PostMapping
    public ResponseEntity<Integer> createOldExam(@RequestBody @Valid final OldExamDTO oldExamDTO) {
        final Integer createdOeId = oldExamService.create(oldExamDTO);
        return new ResponseEntity<>(createdOeId, HttpStatus.CREATED);
    }

    @PutMapping("/{oeId}")
    public ResponseEntity<Integer> updateOldExam(@PathVariable(name = "oeId") final Integer oeId,
            @RequestBody @Valid final OldExamDTO oldExamDTO) {
        oldExamService.update(oeId, oldExamDTO);
        return ResponseEntity.ok(oeId);
    }

    @DeleteMapping("/{oeId}")
    public ResponseEntity<Void> deleteOldExam(@PathVariable(name = "oeId") final Integer oeId) {
        final ReferencedWarning referencedWarning = oldExamService.getReferencedWarning(oeId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        oldExamService.delete(oeId);
        return ResponseEntity.noContent().build();
    }

}
