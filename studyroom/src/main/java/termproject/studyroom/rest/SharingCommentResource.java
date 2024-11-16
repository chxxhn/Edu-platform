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
import termproject.studyroom.model.SharingCommentDTO;
import termproject.studyroom.service.SharingCommentService;


@RestController
@RequestMapping(value = "/api/sharingComments", produces = MediaType.APPLICATION_JSON_VALUE)
public class SharingCommentResource {

    private final SharingCommentService sharingCommentService;

    public SharingCommentResource(final SharingCommentService sharingCommentService) {
        this.sharingCommentService = sharingCommentService;
    }

    @GetMapping
    public ResponseEntity<List<SharingCommentDTO>> getAllSharingComments() {
        return ResponseEntity.ok(sharingCommentService.findAll());
    }

    @GetMapping("/{shcomId}")
    public ResponseEntity<SharingCommentDTO> getSharingComment(
            @PathVariable(name = "shcomId") final Integer shcomId) {
        return ResponseEntity.ok(sharingCommentService.get(shcomId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSharingComment(
            @RequestBody @Valid final SharingCommentDTO sharingCommentDTO) {
        final Integer createdShcomId = sharingCommentService.create(sharingCommentDTO);
        return new ResponseEntity<>(createdShcomId, HttpStatus.CREATED);
    }

    @PutMapping("/{shcomId}")
    public ResponseEntity<Integer> updateSharingComment(
            @PathVariable(name = "shcomId") final Integer shcomId,
            @RequestBody @Valid final SharingCommentDTO sharingCommentDTO) {
        sharingCommentService.update(shcomId, sharingCommentDTO);
        return ResponseEntity.ok(shcomId);
    }

    @DeleteMapping("/{shcomId}")
    public ResponseEntity<Void> deleteSharingComment(
            @PathVariable(name = "shcomId") final Integer shcomId) {
        sharingCommentService.delete(shcomId);
        return ResponseEntity.noContent().build();
    }

}
