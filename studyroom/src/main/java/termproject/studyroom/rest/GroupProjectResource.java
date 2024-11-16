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
import termproject.studyroom.model.GroupProjectDTO;
import termproject.studyroom.service.GroupProjectService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/groupProjects", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupProjectResource {

    private final GroupProjectService groupProjectService;

    public GroupProjectResource(final GroupProjectService groupProjectService) {
        this.groupProjectService = groupProjectService;
    }

    @GetMapping
    public ResponseEntity<List<GroupProjectDTO>> getAllGroupProjects() {
        return ResponseEntity.ok(groupProjectService.findAll());
    }

    @GetMapping("/{gpId}")
    public ResponseEntity<GroupProjectDTO> getGroupProject(
            @PathVariable(name = "gpId") final Integer gpId) {
        return ResponseEntity.ok(groupProjectService.get(gpId));
    }

    @PostMapping
    public ResponseEntity<Integer> createGroupProject(
            @RequestBody @Valid final GroupProjectDTO groupProjectDTO) {
        final Integer createdGpId = groupProjectService.create(groupProjectDTO);
        return new ResponseEntity<>(createdGpId, HttpStatus.CREATED);
    }

    @PutMapping("/{gpId}")
    public ResponseEntity<Integer> updateGroupProject(
            @PathVariable(name = "gpId") final Integer gpId,
            @RequestBody @Valid final GroupProjectDTO groupProjectDTO) {
        groupProjectService.update(gpId, groupProjectDTO);
        return ResponseEntity.ok(gpId);
    }

    @DeleteMapping("/{gpId}")
    public ResponseEntity<Void> deleteGroupProject(
            @PathVariable(name = "gpId") final Integer gpId) {
        final ReferencedWarning referencedWarning = groupProjectService.getReferencedWarning(gpId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        groupProjectService.delete(gpId);
        return ResponseEntity.noContent().build();
    }

}
