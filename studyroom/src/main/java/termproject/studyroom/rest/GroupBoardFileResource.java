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
import termproject.studyroom.model.GroupBoardFileDTO;
import termproject.studyroom.service.GroupBoardFileService;


@RestController
@RequestMapping(value = "/api/groupBoardFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupBoardFileResource {

    private final GroupBoardFileService groupBoardFileService;

    public GroupBoardFileResource(final GroupBoardFileService groupBoardFileService) {
        this.groupBoardFileService = groupBoardFileService;
    }

    @GetMapping
    public ResponseEntity<List<GroupBoardFileDTO>> getAllGroupBoardFiles() {
        return ResponseEntity.ok(groupBoardFileService.findAll());
    }

    @GetMapping("/{gbfId}")
    public ResponseEntity<GroupBoardFileDTO> getGroupBoardFile(
            @PathVariable(name = "gbfId") final Integer gbfId) {
        return ResponseEntity.ok(groupBoardFileService.get(gbfId));
    }

    @PostMapping
    public ResponseEntity<Integer> createGroupBoardFile(
            @RequestBody @Valid final GroupBoardFileDTO groupBoardFileDTO) {
        final Integer createdGbfId = groupBoardFileService.create(groupBoardFileDTO);
        return new ResponseEntity<>(createdGbfId, HttpStatus.CREATED);
    }

    @PutMapping("/{gbfId}")
    public ResponseEntity<Integer> updateGroupBoardFile(
            @PathVariable(name = "gbfId") final Integer gbfId,
            @RequestBody @Valid final GroupBoardFileDTO groupBoardFileDTO) {
        groupBoardFileService.update(gbfId, groupBoardFileDTO);
        return ResponseEntity.ok(gbfId);
    }

    @DeleteMapping("/{gbfId}")
    public ResponseEntity<Void> deleteGroupBoardFile(
            @PathVariable(name = "gbfId") final Integer gbfId) {
        groupBoardFileService.delete(gbfId);
        return ResponseEntity.noContent().build();
    }

}
