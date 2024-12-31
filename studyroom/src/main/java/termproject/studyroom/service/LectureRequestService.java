package termproject.studyroom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.LectureRequestDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureRequestRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class LectureRequestService {

    private final LectureRequestRepository lectureRequestRepository;
    private final LectureListRepository lectureListRepository;
    private final UserRepository userRepository;

    public LectureRequestService(final LectureRequestRepository lectureRequestRepository,
                                 final LectureListRepository lectureListRepository, UserRepository userRepository) {
        this.lectureRequestRepository = lectureRequestRepository;
        this.lectureListRepository = lectureListRepository;
        this.userRepository = userRepository;
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

    public Page<LectureRequest> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "rqId"));
        return this.lectureRequestRepository.findAll(pageable);
    }

    public Integer create(final LectureRequestDTO lectureRequestDTO) {
        final LectureRequest lectureRequest = new LectureRequest();
        mapToEntity(lectureRequestDTO, lectureRequest);
        if(lectureRequest.getLikeCount()==null){
            lectureRequest.setLikeCount(0);
        }
        if(lectureRequest.getWarnCount()==null){
            lectureRequest.setWarnCount(0);
        }
        if(lectureRequest.getLectureValid()==null){
            lectureRequest.setLectureValid(false);
        }
        return lectureRequestRepository.save(lectureRequest).getRqId();
    }

    public void update(final Integer rqId, final LectureRequestDTO lectureRequestDTO) {
        final LectureRequest lectureRequest = lectureRequestRepository.findById(rqId)
                .orElseThrow(NotFoundException::new);
        lectureRequest.setTitle(lectureRequestDTO.getTitle());
        lectureRequest.setContent(lectureRequestDTO.getContent());
        if (lectureRequestDTO.getLikeCount() != null) {
            lectureRequest.setLikeCount(lectureRequestDTO.getLikeCount());
        }
        if (lectureRequestDTO.getWarnCount() != null) {
            lectureRequest.setWarnCount(lectureRequestDTO.getWarnCount());
        }
        if (lectureRequestDTO.getLectureValid() != null) {
            lectureRequest.setLectureValid(lectureRequestDTO.getLectureValid());
        }
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
        lectureRequestDTO.setLikeCount(lectureRequest.getLikeCount());
        lectureRequestDTO.setWarnCount(lectureRequest.getWarnCount());
        lectureRequestDTO.setLectureValid(lectureRequest.getLectureValid());
        lectureRequestDTO.setAuthor(lectureRequest.getAuthor() == null ? null : lectureRequest.getAuthor());
        lectureRequestDTO.setLectureId(lectureRequest.getLectureId() == null ? null : lectureRequest.getLectureId());
        return lectureRequestDTO;
    }

    private LectureRequest mapToEntity(final LectureRequestDTO lectureRequestDTO,
            final LectureRequest lectureRequest) {
        lectureRequest.setTitle(lectureRequestDTO.getTitle());
        lectureRequest.setContent(lectureRequestDTO.getContent());
        lectureRequest.setLectureValid(lectureRequestDTO.getLectureValid());
        lectureRequest.setLikeCount(lectureRequestDTO.getLikeCount());
        lectureRequest.setWarnCount(lectureRequestDTO.getWarnCount());
        final LectureList lectureId = lectureRequestDTO.getLectureId() == null ? null : lectureListRepository.findById(lectureRequestDTO.getLectureId().getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        lectureRequest.setLectureId(lectureId);
        final User author = lectureRequestDTO.getAuthor() == null ? null : userRepository.findById(lectureRequestDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("author not found"));
        lectureRequest.setAuthor(author);
        return lectureRequest;
    }

}
