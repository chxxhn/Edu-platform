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
import termproject.studyroom.model.LectureListDTO;
import termproject.studyroom.service.LectureListService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/lectureLists", produces = MediaType.APPLICATION_JSON_VALUE)
public class LectureListResource {

    private final LectureListService lectureListService;

    public LectureListResource(final LectureListService lectureListService) {
        this.lectureListService = lectureListService;
    }

    @GetMapping
    public ResponseEntity<List<LectureListDTO>> getAllLectureLists() {
        return ResponseEntity.ok(lectureListService.findAll());
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<LectureListDTO> getLectureList(
            @PathVariable(name = "lectureId") final Integer lectureId) {
        return ResponseEntity.ok(lectureListService.get(lectureId));
    }

    @PostMapping
    public ResponseEntity<Integer> createLectureList(
            @RequestBody @Valid final LectureListDTO lectureListDTO) {
        final Integer createdLectureId = lectureListService.create(lectureListDTO);
        return new ResponseEntity<>(createdLectureId, HttpStatus.CREATED);
    }

    @PutMapping("/{lectureId}")
    public ResponseEntity<Integer> updateLectureList(
            @PathVariable(name = "lectureId") final Integer lectureId,
            @RequestBody @Valid final LectureListDTO lectureListDTO) {
        lectureListService.update(lectureId, lectureListDTO);
        return ResponseEntity.ok(lectureId);
    }

    @DeleteMapping("/{lectureId}")
    public ResponseEntity<Void> deleteLectureList(
            @PathVariable(name = "lectureId") final Integer lectureId) {
        final ReferencedWarning referencedWarning = lectureListService.getReferencedWarning(lectureId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        lectureListService.delete(lectureId);
        return ResponseEntity.noContent().build();
    }

}
