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
import termproject.studyroom.model.OldExamFileDTO;
import termproject.studyroom.service.OldExamFileService;


@RestController
@RequestMapping(value = "/api/oldExamFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class OldExamFileResource {

    private final OldExamFileService oldExamFileService;

    public OldExamFileResource(final OldExamFileService oldExamFileService) {
        this.oldExamFileService = oldExamFileService;
    }

    @GetMapping
    public ResponseEntity<List<OldExamFileDTO>> getAllOldExamFiles() {
        return ResponseEntity.ok(oldExamFileService.findAll());
    }

    @GetMapping("/{oefId}")
    public ResponseEntity<OldExamFileDTO> getOldExamFile(
            @PathVariable(name = "oefId") final Integer oefId) {
        return ResponseEntity.ok(oldExamFileService.get(oefId));
    }

    @PostMapping
    public ResponseEntity<Integer> createOldExamFile(
            @RequestBody @Valid final OldExamFileDTO oldExamFileDTO) {
        final Integer createdOefId = oldExamFileService.create(oldExamFileDTO);
        return new ResponseEntity<>(createdOefId, HttpStatus.CREATED);
    }

    @PutMapping("/{oefId}")
    public ResponseEntity<Integer> updateOldExamFile(
            @PathVariable(name = "oefId") final Integer oefId,
            @RequestBody @Valid final OldExamFileDTO oldExamFileDTO) {
        oldExamFileService.update(oefId, oldExamFileDTO);
        return ResponseEntity.ok(oefId);
    }

    @DeleteMapping("/{oefId}")
    public ResponseEntity<Void> deleteOldExamFile(
            @PathVariable(name = "oefId") final Integer oefId) {
        oldExamFileService.delete(oefId);
        return ResponseEntity.noContent().build();
    }

}
