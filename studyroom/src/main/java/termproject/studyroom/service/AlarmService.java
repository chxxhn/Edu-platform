package termproject.studyroom.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.Alarm;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.AlarmDTO;
import termproject.studyroom.repos.AlarmRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public AlarmService(final AlarmRepository alarmRepository,
            final UserRepository userRepository,
                        final LectureListRepository lectureListRepository) {
        this.alarmRepository = alarmRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }


    public void markAsRead(Integer alarmId) {
        // get 메서드로 AlarmDTO 가져오기
        AlarmDTO alarmDTO = get(alarmId);
        // 읽음 상태 변경
        alarmDTO.setReadState(true);
        // update 메서드를 이용해 상태 업데이트
        update(alarmId, alarmDTO);
    }



    public List<AlarmDTO> findAll() {
        final List<Alarm> alarms = alarmRepository.findAll(Sort.by("alarmId"));
        return alarms.stream()
                .map(alarm -> mapToDTO(alarm, new AlarmDTO()))
                .toList();
    }

    public List<AlarmDTO> findAlarm(User user) {
        final List<Alarm> alarms = alarmRepository.findByUserId(user);
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
        alarmDTO.setUrl(alarm.getUrl());
        alarmDTO.setContent(alarm.getContent());
        alarmDTO.setAlarmType(alarm.getAlarmType());
        alarmDTO.setReadState(alarm.getReadState());
        alarmDTO.setLectureId(alarm.getLectureId() == null ? null : alarm.getLectureId().getLectureId());
        alarmDTO.setBoardId(alarm.getBoardId());
        alarmDTO.setUserId(alarm.getUserId() == null ? null : alarm.getUserId().getStdId());
        return alarmDTO;
    }

    private Alarm mapToEntity(final AlarmDTO alarmDTO, final Alarm alarm) {
        alarm.setContent(alarmDTO.getContent());
        alarm.setAlarmType(alarmDTO.getAlarmType());
        alarm.setReadState(alarmDTO.getReadState());
        alarm.setBoardId(alarmDTO.getBoardId());
        alarm.setUrl(alarmDTO.getUrl());
        final LectureList lectureId = alarmDTO.getLectureId() == null ? null : lectureListRepository.findById(alarmDTO.getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        alarm.setLectureId(lectureId);

        final User userId = alarmDTO.getUserId() == null ? null : userRepository.findById(alarmDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("userId not found"));
        alarm.setUserId(userId);
        return alarm;
    }

}
