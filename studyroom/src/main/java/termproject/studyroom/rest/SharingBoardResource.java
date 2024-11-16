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
import termproject.studyroom.model.SharingBoardDTO;
import termproject.studyroom.service.SharingBoardService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/sharingBoards", produces = MediaType.APPLICATION_JSON_VALUE)
public class SharingBoardResource {

    private final SharingBoardService sharingBoardService;

    public SharingBoardResource(final SharingBoardService sharingBoardService) {
        this.sharingBoardService = sharingBoardService;
    }

    @GetMapping
    public ResponseEntity<List<SharingBoardDTO>> getAllSharingBoards() {
        return ResponseEntity.ok(sharingBoardService.findAll());
    }

    @GetMapping("/{sharingId}")
    public ResponseEntity<SharingBoardDTO> getSharingBoard(
            @PathVariable(name = "sharingId") final Integer sharingId) {
        return ResponseEntity.ok(sharingBoardService.get(sharingId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSharingBoard(
            @RequestBody @Valid final SharingBoardDTO sharingBoardDTO) {
        final Integer createdSharingId = sharingBoardService.create(sharingBoardDTO);
        return new ResponseEntity<>(createdSharingId, HttpStatus.CREATED);
    }

    @PutMapping("/{sharingId}")
    public ResponseEntity<Integer> updateSharingBoard(
            @PathVariable(name = "sharingId") final Integer sharingId,
            @RequestBody @Valid final SharingBoardDTO sharingBoardDTO) {
        sharingBoardService.update(sharingId, sharingBoardDTO);
        return ResponseEntity.ok(sharingId);
    }

    @DeleteMapping("/{sharingId}")
    public ResponseEntity<Void> deleteSharingBoard(
            @PathVariable(name = "sharingId") final Integer sharingId) {
        final ReferencedWarning referencedWarning = sharingBoardService.getReferencedWarning(sharingId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        sharingBoardService.delete(sharingId);
        return ResponseEntity.noContent().build();
    }

}
