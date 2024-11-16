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
import termproject.studyroom.model.AlarmDTO;
import termproject.studyroom.service.AlarmService;


@RestController
@RequestMapping(value = "/api/alarms", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlarmResource {

    private final AlarmService alarmService;

    public AlarmResource(final AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping
    public ResponseEntity<List<AlarmDTO>> getAllAlarms() {
        return ResponseEntity.ok(alarmService.findAll());
    }

    @GetMapping("/{alarmId}")
    public ResponseEntity<AlarmDTO> getAlarm(
            @PathVariable(name = "alarmId") final Integer alarmId) {
        return ResponseEntity.ok(alarmService.get(alarmId));
    }

    @PostMapping
    public ResponseEntity<Integer> createAlarm(@RequestBody @Valid final AlarmDTO alarmDTO) {
        final Integer createdAlarmId = alarmService.create(alarmDTO);
        return new ResponseEntity<>(createdAlarmId, HttpStatus.CREATED);
    }

    @PutMapping("/{alarmId}")
    public ResponseEntity<Integer> updateAlarm(
            @PathVariable(name = "alarmId") final Integer alarmId,
            @RequestBody @Valid final AlarmDTO alarmDTO) {
        alarmService.update(alarmId, alarmDTO);
        return ResponseEntity.ok(alarmId);
    }

    @DeleteMapping("/{alarmId}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable(name = "alarmId") final Integer alarmId) {
        alarmService.delete(alarmId);
        return ResponseEntity.noContent().build();
    }

}
