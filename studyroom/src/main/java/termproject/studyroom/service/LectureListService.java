package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.CommunicationBoard;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.LectureListDTO;
import termproject.studyroom.repos.CommunicationBoardRepository;
import termproject.studyroom.repos.GroupProjectRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureRequestRepository;
import termproject.studyroom.repos.NoticeBoardRepository;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class LectureListService {

    private final LectureListRepository lectureListRepository;
    private final UserRepository userRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final OldExamRepository oldExamRepository;
    private final LectureRequestRepository lectureRequestRepository;
    private final CommunicationBoardRepository communicationBoardRepository;

    public LectureListService(final LectureListRepository lectureListRepository,
            final UserRepository userRepository,
            final GroupProjectRepository groupProjectRepository,
            final NoticeBoardRepository noticeBoardRepository,
            final QuestionBoardRepository questionBoardRepository,
            final SharingBoardRepository sharingBoardRepository,
            final OldExamRepository oldExamRepository,
            final LectureRequestRepository lectureRequestRepository,
            final CommunicationBoardRepository communicationBoardRepository) {
        this.lectureListRepository = lectureListRepository;
        this.userRepository = userRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.noticeBoardRepository = noticeBoardRepository;
        this.questionBoardRepository = questionBoardRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.oldExamRepository = oldExamRepository;
        this.lectureRequestRepository = lectureRequestRepository;
        this.communicationBoardRepository = communicationBoardRepository;
    }

    public List<LectureListDTO> findAll() {
        final List<LectureList> lectureLists = lectureListRepository.findAll(Sort.by("lectureId"));
        return lectureLists.stream()
                .map(lectureList -> mapToDTO(lectureList, new LectureListDTO()))
                .toList();
    }

    public LectureListDTO get(final Integer lectureId) {
        return lectureListRepository.findById(lectureId)
                .map(lectureList -> mapToDTO(lectureList, new LectureListDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LectureListDTO lectureListDTO) {
        final LectureList lectureList = new LectureList();
        mapToEntity(lectureListDTO, lectureList);
        return lectureListRepository.save(lectureList).getLectureId();
    }

    public void update(final Integer lectureId, final LectureListDTO lectureListDTO) {
        final LectureList lectureList = lectureListRepository.findById(lectureId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lectureListDTO, lectureList);
        lectureListRepository.save(lectureList);
    }

    public void delete(final Integer lectureId) {
        lectureListRepository.deleteById(lectureId);
    }

    private LectureListDTO mapToDTO(final LectureList lectureList,
            final LectureListDTO lectureListDTO) {
        lectureListDTO.setLectureId(lectureList.getLectureId());
        lectureListDTO.setName(lectureList.getName());
        lectureListDTO.setStdId(lectureList.getStdId() == null ? null : lectureList.getStdId().getStdId());
        return lectureListDTO;
    }

    private LectureList mapToEntity(final LectureListDTO lectureListDTO,
            final LectureList lectureList) {
        lectureList.setName(lectureListDTO.getName());
        lectureList.setLectureId(lectureListDTO.getLectureId());
        final User stdId = lectureListDTO.getStdId() == null ? null : userRepository.findById(lectureListDTO.getStdId())
                .orElseThrow(() -> new NotFoundException("stdId not found"));
        lectureList.setStdId(stdId);
        return lectureList;
    }

    public boolean nameExists(final String name) {
        return lectureListRepository.existsByNameIgnoreCase(name);
    }

    public ReferencedWarning getReferencedWarning(final Integer lectureId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final LectureList lectureList = lectureListRepository.findById(lectureId)
                .orElseThrow(NotFoundException::new);
        final GroupProject lectureIdGroupProject = groupProjectRepository.findFirstByLectureId(lectureList);
        if (lectureIdGroupProject != null) {
            referencedWarning.setKey("lectureList.groupProject.lectureId.referenced");
            referencedWarning.addParam(lectureIdGroupProject.getGpId());
            return referencedWarning;
        }
        final NoticeBoard lectureIdNoticeBoard = noticeBoardRepository.findFirstByLectureId(lectureList);
        if (lectureIdNoticeBoard != null) {
            referencedWarning.setKey("lectureList.noticeBoard.lectureId.referenced");
            referencedWarning.addParam(lectureIdNoticeBoard.getNoticeId());
            return referencedWarning;
        }
        final QuestionBoard lectureIdQuestionBoard = questionBoardRepository.findFirstByLectureId(lectureList);
        if (lectureIdQuestionBoard != null) {
            referencedWarning.setKey("lectureList.questionBoard.lectureId.referenced");
            referencedWarning.addParam(lectureIdQuestionBoard.getQuestionId());
            return referencedWarning;
        }
        final SharingBoard lectureIdSharingBoard = sharingBoardRepository.findFirstByLectureId(lectureList);
        if (lectureIdSharingBoard != null) {
            referencedWarning.setKey("lectureList.sharingBoard.lectureId.referenced");
            referencedWarning.addParam(lectureIdSharingBoard.getSharingId());
            return referencedWarning;
        }
        final OldExam lectureIdOldExam = oldExamRepository.findFirstByLectureId(lectureList);
        if (lectureIdOldExam != null) {
            referencedWarning.setKey("lectureList.oldExam.lectureId.referenced");
            referencedWarning.addParam(lectureIdOldExam.getOeId());
            return referencedWarning;
        }
        final LectureRequest lectureIdLectureRequest = lectureRequestRepository.findFirstByLectureId(lectureList);
        if (lectureIdLectureRequest != null) {
            referencedWarning.setKey("lectureList.lectureRequest.lectureId.referenced");
            referencedWarning.addParam(lectureIdLectureRequest.getRqId());
            return referencedWarning;
        }
        final CommunicationBoard lectureIdCommunicationBoard = communicationBoardRepository.findFirstByLectureId(lectureList);
        if (lectureIdCommunicationBoard != null) {
            referencedWarning.setKey("lectureList.communicationBoard.lectureId.referenced");
            referencedWarning.addParam(lectureIdCommunicationBoard.getComnId());
            return referencedWarning;
        }
        return null;
    }

}
