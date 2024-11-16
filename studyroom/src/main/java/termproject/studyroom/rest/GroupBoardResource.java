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
import termproject.studyroom.model.GroupBoardDTO;
import termproject.studyroom.service.GroupBoardService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/groupBoards", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupBoardResource {

    private final GroupBoardService groupBoardService;

    public GroupBoardResource(final GroupBoardService groupBoardService) {
        this.groupBoardService = groupBoardService;
    }

    @GetMapping
    public ResponseEntity<List<GroupBoardDTO>> getAllGroupBoards() {
        return ResponseEntity.ok(groupBoardService.findAll());
    }

    @GetMapping("/{gbId}")
    public ResponseEntity<GroupBoardDTO> getGroupBoard(
            @PathVariable(name = "gbId") final Integer gbId) {
        return ResponseEntity.ok(groupBoardService.get(gbId));
    }

    @PostMapping
    public ResponseEntity<Integer> createGroupBoard(
            @RequestBody @Valid final GroupBoardDTO groupBoardDTO) {
        final Integer createdGbId = groupBoardService.create(groupBoardDTO);
        return new ResponseEntity<>(createdGbId, HttpStatus.CREATED);
    }

    @PutMapping("/{gbId}")
    public ResponseEntity<Integer> updateGroupBoard(@PathVariable(name = "gbId") final Integer gbId,
            @RequestBody @Valid final GroupBoardDTO groupBoardDTO) {
        groupBoardService.update(gbId, groupBoardDTO);
        return ResponseEntity.ok(gbId);
    }

    @DeleteMapping("/{gbId}")
    public ResponseEntity<Void> deleteGroupBoard(@PathVariable(name = "gbId") final Integer gbId) {
        final ReferencedWarning referencedWarning = groupBoardService.getReferencedWarning(gbId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        groupBoardService.delete(gbId);
        return ResponseEntity.noContent().build();
    }

}
