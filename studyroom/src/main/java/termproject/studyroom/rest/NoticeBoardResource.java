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
import termproject.studyroom.model.NoticeBoardDTO;
import termproject.studyroom.service.NoticeBoardService;


@RestController
@RequestMapping(value = "/api/noticeBoards", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeBoardResource {

    private final NoticeBoardService noticeBoardService;

    public NoticeBoardResource(final NoticeBoardService noticeBoardService) {
        this.noticeBoardService = noticeBoardService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeBoardDTO>> getAllNoticeBoards() {
        return ResponseEntity.ok(noticeBoardService.findAll());
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeBoardDTO> getNoticeBoard(
            @PathVariable(name = "noticeId") final Integer noticeId) {
        return ResponseEntity.ok(noticeBoardService.get(noticeId));
    }

    @PostMapping
    public ResponseEntity<Integer> createNoticeBoard(
            @RequestBody @Valid final NoticeBoardDTO noticeBoardDTO) {
        final Integer createdNoticeId = noticeBoardService.create(noticeBoardDTO);
        return new ResponseEntity<>(createdNoticeId, HttpStatus.CREATED);
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<Integer> updateNoticeBoard(
            @PathVariable(name = "noticeId") final Integer noticeId,
            @RequestBody @Valid final NoticeBoardDTO noticeBoardDTO) {
        noticeBoardService.update(noticeId, noticeBoardDTO);
        return ResponseEntity.ok(noticeId);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNoticeBoard(
            @PathVariable(name = "noticeId") final Integer noticeId) {
        noticeBoardService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }

}
