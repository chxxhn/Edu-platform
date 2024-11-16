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
import termproject.studyroom.model.SharingFileDTO;
import termproject.studyroom.service.SharingFileService;


@RestController
@RequestMapping(value = "/api/sharingFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class SharingFileResource {

    private final SharingFileService sharingFileService;

    public SharingFileResource(final SharingFileService sharingFileService) {
        this.sharingFileService = sharingFileService;
    }

    @GetMapping
    public ResponseEntity<List<SharingFileDTO>> getAllSharingFiles() {
        return ResponseEntity.ok(sharingFileService.findAll());
    }

    @GetMapping("/{shfId}")
    public ResponseEntity<SharingFileDTO> getSharingFile(
            @PathVariable(name = "shfId") final Integer shfId) {
        return ResponseEntity.ok(sharingFileService.get(shfId));
    }

    @PostMapping
    public ResponseEntity<Integer> createSharingFile(
            @RequestBody @Valid final SharingFileDTO sharingFileDTO) {
        final Integer createdShfId = sharingFileService.create(sharingFileDTO);
        return new ResponseEntity<>(createdShfId, HttpStatus.CREATED);
    }

    @PutMapping("/{shfId}")
    public ResponseEntity<Integer> updateSharingFile(
            @PathVariable(name = "shfId") final Integer shfId,
            @RequestBody @Valid final SharingFileDTO sharingFileDTO) {
        sharingFileService.update(shfId, sharingFileDTO);
        return ResponseEntity.ok(shfId);
    }

    @DeleteMapping("/{shfId}")
    public ResponseEntity<Void> deleteSharingFile(
            @PathVariable(name = "shfId") final Integer shfId) {
        sharingFileService.delete(shfId);
        return ResponseEntity.noContent().build();
    }

}
