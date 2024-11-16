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
import termproject.studyroom.model.LectureRequestDTO;
import termproject.studyroom.service.LectureRequestService;


@RestController
@RequestMapping(value = "/api/lectureRequests", produces = MediaType.APPLICATION_JSON_VALUE)
public class LectureRequestResource {

    private final LectureRequestService lectureRequestService;

    public LectureRequestResource(final LectureRequestService lectureRequestService) {
        this.lectureRequestService = lectureRequestService;
    }

    @GetMapping
    public ResponseEntity<List<LectureRequestDTO>> getAllLectureRequests() {
        return ResponseEntity.ok(lectureRequestService.findAll());
    }

    @GetMapping("/{rqId}")
    public ResponseEntity<LectureRequestDTO> getLectureRequest(
            @PathVariable(name = "rqId") final Integer rqId) {
        return ResponseEntity.ok(lectureRequestService.get(rqId));
    }

    @PostMapping
    public ResponseEntity<Integer> createLectureRequest(
            @RequestBody @Valid final LectureRequestDTO lectureRequestDTO) {
        final Integer createdRqId = lectureRequestService.create(lectureRequestDTO);
        return new ResponseEntity<>(createdRqId, HttpStatus.CREATED);
    }

    @PutMapping("/{rqId}")
    public ResponseEntity<Integer> updateLectureRequest(
            @PathVariable(name = "rqId") final Integer rqId,
            @RequestBody @Valid final LectureRequestDTO lectureRequestDTO) {
        lectureRequestService.update(rqId, lectureRequestDTO);
        return ResponseEntity.ok(rqId);
    }

    @DeleteMapping("/{rqId}")
    public ResponseEntity<Void> deleteLectureRequest(
            @PathVariable(name = "rqId") final Integer rqId) {
        lectureRequestService.delete(rqId);
        return ResponseEntity.noContent().build();
    }

}
