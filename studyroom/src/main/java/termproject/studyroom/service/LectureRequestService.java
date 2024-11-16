package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.model.LectureRequestDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureRequestRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class LectureRequestService {

    private final LectureRequestRepository lectureRequestRepository;
    private final LectureListRepository lectureListRepository;

    public LectureRequestService(final LectureRequestRepository lectureRequestRepository,
            final LectureListRepository lectureListRepository) {
        this.lectureRequestRepository = lectureRequestRepository;
        this.lectureListRepository = lectureListRepository;
    }

    public List<LectureRequestDTO> findAll() {
        final List<LectureRequest> lectureRequests = lectureRequestRepository.findAll(Sort.by("rqId"));
        return lectureRequests.stream()
                .map(lectureRequest -> mapToDTO(lectureRequest, new LectureRequestDTO()))
                .toList();
    }

    public LectureRequestDTO get(final Integer rqId) {
        return lectureRequestRepository.findById(rqId)
                .map(lectureRequest -> mapToDTO(lectureRequest, new LectureRequestDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LectureRequestDTO lectureRequestDTO) {
        final LectureRequest lectureRequest = new LectureRequest();
        mapToEntity(lectureRequestDTO, lectureRequest);
        return lectureRequestRepository.save(lectureRequest).getRqId();
    }

    public void update(final Integer rqId, final LectureRequestDTO lectureRequestDTO) {
        final LectureRequest lectureRequest = lectureRequestRepository.findById(rqId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lectureRequestDTO, lectureRequest);
        lectureRequestRepository.save(lectureRequest);
    }

    public void delete(final Integer rqId) {
        lectureRequestRepository.deleteById(rqId);
    }

    private LectureRequestDTO mapToDTO(final LectureRequest lectureRequest,
            final LectureRequestDTO lectureRequestDTO) {
        lectureRequestDTO.setRqId(lectureRequest.getRqId());
        lectureRequestDTO.setTitle(lectureRequest.getTitle());
        lectureRequestDTO.setContent(lectureRequest.getContent());
        lectureRequestDTO.setNumberDesired(lectureRequest.getNumberDesired());
        lectureRequestDTO.setLectureValid(lectureRequest.getLectureValid());
        lectureRequestDTO.setLectureId(lectureRequest.getLectureId() == null ? null : lectureRequest.getLectureId().getLectureId());
        return lectureRequestDTO;
    }

    private LectureRequest mapToEntity(final LectureRequestDTO lectureRequestDTO,
            final LectureRequest lectureRequest) {
        lectureRequest.setTitle(lectureRequestDTO.getTitle());
        lectureRequest.setContent(lectureRequestDTO.getContent());
        lectureRequest.setNumberDesired(lectureRequestDTO.getNumberDesired());
        lectureRequest.setLectureValid(lectureRequestDTO.getLectureValid());
        final LectureList lectureId = lectureRequestDTO.getLectureId() == null ? null : lectureListRepository.findById(lectureRequestDTO.getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        lectureRequest.setLectureId(lectureId);
        return lectureRequest;
    }

}
