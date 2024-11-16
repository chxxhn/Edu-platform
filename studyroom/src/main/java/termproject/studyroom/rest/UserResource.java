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
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.ReferencedException;
import termproject.studyroom.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{stdId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "stdId") final Integer stdId) {
        return ResponseEntity.ok(userService.get(stdId));
    }

    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Integer createdStdId = userService.create(userDTO);
        return new ResponseEntity<>(createdStdId, HttpStatus.CREATED);
    }

    @PutMapping("/{stdId}")
    public ResponseEntity<Integer> updateUser(@PathVariable(name = "stdId") final Integer stdId,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(stdId, userDTO);
        return ResponseEntity.ok(stdId);
    }

    @DeleteMapping("/{stdId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "stdId") final Integer stdId) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(stdId);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(stdId);
        return ResponseEntity.noContent().build();
    }

}
