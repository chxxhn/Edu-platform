package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.Alarm;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.AlarmDTO;
import termproject.studyroom.repos.AlarmRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    public AlarmService(final AlarmRepository alarmRepository,
            final UserRepository userRepository) {
        this.alarmRepository = alarmRepository;
        this.userRepository = userRepository;
    }

    public List<AlarmDTO> findAll() {
        final List<Alarm> alarms = alarmRepository.findAll(Sort.by("alarmId"));
        return alarms.stream()
                .map(alarm -> mapToDTO(alarm, new AlarmDTO()))
                .toList();
    }

    public AlarmDTO get(final Integer alarmId) {
        return alarmRepository.findById(alarmId)
                .map(alarm -> mapToDTO(alarm, new AlarmDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AlarmDTO alarmDTO) {
        final Alarm alarm = new Alarm();
        mapToEntity(alarmDTO, alarm);
        return alarmRepository.save(alarm).getAlarmId();
    }

    public void update(final Integer alarmId, final AlarmDTO alarmDTO) {
        final Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(alarmDTO, alarm);
        alarmRepository.save(alarm);
    }

    public void delete(final Integer alarmId) {
        alarmRepository.deleteById(alarmId);
    }

    private AlarmDTO mapToDTO(final Alarm alarm, final AlarmDTO alarmDTO) {
        alarmDTO.setAlarmId(alarm.getAlarmId());
        alarmDTO.setContent(alarm.getContent());
        alarmDTO.setAlarmType(alarm.getAlarmType());
        alarmDTO.setReadState(alarm.getReadState());
        alarmDTO.setUserId(alarm.getUserId() == null ? null : alarm.getUserId().getStdId());
        return alarmDTO;
    }

    private Alarm mapToEntity(final AlarmDTO alarmDTO, final Alarm alarm) {
        alarm.setContent(alarmDTO.getContent());
        alarm.setAlarmType(alarmDTO.getAlarmType());
        alarm.setReadState(alarmDTO.getReadState());
        final User userId = alarmDTO.getUserId() == null ? null : userRepository.findById(alarmDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("userId not found"));
        alarm.setUserId(userId);
        return alarm;
    }

}
