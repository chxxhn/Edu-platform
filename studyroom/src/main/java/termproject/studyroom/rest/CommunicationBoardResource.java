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
import termproject.studyroom.model.CommunicationBoardDTO;
import termproject.studyroom.service.CommunicationBoardService;


@RestController
@RequestMapping(value = "/api/communicationBoards", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommunicationBoardResource {

    private final CommunicationBoardService communicationBoardService;

    public CommunicationBoardResource(final CommunicationBoardService communicationBoardService) {
        this.communicationBoardService = communicationBoardService;
    }

    @GetMapping
    public ResponseEntity<List<CommunicationBoardDTO>> getAllCommunicationBoards() {
        return ResponseEntity.ok(communicationBoardService.findAll());
    }

    @GetMapping("/{comnId}")
    public ResponseEntity<CommunicationBoardDTO> getCommunicationBoard(
            @PathVariable(name = "comnId") final Integer comnId) {
        return ResponseEntity.ok(communicationBoardService.get(comnId));
    }

    @PostMapping
    public ResponseEntity<Integer> createCommunicationBoard(
            @RequestBody @Valid final CommunicationBoardDTO communicationBoardDTO) {
        final Integer createdComnId = communicationBoardService.create(communicationBoardDTO);
        return new ResponseEntity<>(createdComnId, HttpStatus.CREATED);
    }

    @PutMapping("/{comnId}")
    public ResponseEntity<Integer> updateCommunicationBoard(
            @PathVariable(name = "comnId") final Integer comnId,
            @RequestBody @Valid final CommunicationBoardDTO communicationBoardDTO) {
        communicationBoardService.update(comnId, communicationBoardDTO);
        return ResponseEntity.ok(comnId);
    }

    @DeleteMapping("/{comnId}")
    public ResponseEntity<Void> deleteCommunicationBoard(
            @PathVariable(name = "comnId") final Integer comnId) {
        communicationBoardService.delete(comnId);
        return ResponseEntity.noContent().build();
    }

}
