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
import termproject.studyroom.model.GroupBoardCommentDTO;
import termproject.studyroom.service.GroupBoardCommentService;


@RestController
@RequestMapping(value = "/api/groupBoardComments", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupBoardCommentResource {

    private final GroupBoardCommentService groupBoardCommentService;

    public GroupBoardCommentResource(final GroupBoardCommentService groupBoardCommentService) {
        this.groupBoardCommentService = groupBoardCommentService;
    }

    @GetMapping
    public ResponseEntity<List<GroupBoardCommentDTO>> getAllGroupBoardComments() {
        return ResponseEntity.ok(groupBoardCommentService.findAll());
    }

    @GetMapping("/{gbcomId}")
    public ResponseEntity<GroupBoardCommentDTO> getGroupBoardComment(
            @PathVariable(name = "gbcomId") final Integer gbcomId) {
        return ResponseEntity.ok(groupBoardCommentService.get(gbcomId));
    }

    @PostMapping
    public ResponseEntity<Integer> createGroupBoardComment(
            @RequestBody @Valid final GroupBoardCommentDTO groupBoardCommentDTO) {
        final Integer createdGbcomId = groupBoardCommentService.create(groupBoardCommentDTO);
        return new ResponseEntity<>(createdGbcomId, HttpStatus.CREATED);
    }

    @PutMapping("/{gbcomId}")
    public ResponseEntity<Integer> updateGroupBoardComment(
            @PathVariable(name = "gbcomId") final Integer gbcomId,
            @RequestBody @Valid final GroupBoardCommentDTO groupBoardCommentDTO) {
        groupBoardCommentService.update(gbcomId, groupBoardCommentDTO);
        return ResponseEntity.ok(gbcomId);
    }

    @DeleteMapping("/{gbcomId}")
    public ResponseEntity<Void> deleteGroupBoardComment(
            @PathVariable(name = "gbcomId") final Integer gbcomId) {
        groupBoardCommentService.delete(gbcomId);
        return ResponseEntity.noContent().build();
    }

}
