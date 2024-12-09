package termproject.studyroom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.CommunicationBoard;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.CommunicationBoardDTO;
import termproject.studyroom.repos.CommunicationBoardRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class CommunicationBoardService {

    private final CommunicationBoardRepository communicationBoardRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public CommunicationBoardService(
            final CommunicationBoardRepository communicationBoardRepository,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.communicationBoardRepository = communicationBoardRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }

    public List<CommunicationBoardDTO> findAll() {
        final List<CommunicationBoard> communicationBoards = communicationBoardRepository.findAll(Sort.by("comnId"));
        return communicationBoards.stream()
                .map(communicationBoard -> mapToDTO(communicationBoard, new CommunicationBoardDTO()))
                .toList();
    }

    public CommunicationBoardDTO get(final Integer comnId) {
        return communicationBoardRepository.findById(comnId)
                .map(communicationBoard -> mapToDTO(communicationBoard, new CommunicationBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Page<CommunicationBoard> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "comnId"));
        return this.communicationBoardRepository.findAll(pageable);
    }

    public Integer create(final CommunicationBoardDTO communicationBoardDTO) {
        final CommunicationBoard communicationBoard = new CommunicationBoard();
        mapToEntity(communicationBoardDTO, communicationBoard);
        if(communicationBoard.getLikeCount()==null){
            communicationBoard.setLikeCount(0);
        }
        return communicationBoardRepository.save(communicationBoard).getComnId();
    }

    public void update(final Integer comnId, final CommunicationBoardDTO communicationBoardDTO) {
        final CommunicationBoard communicationBoard = communicationBoardRepository.findById(comnId)
                .orElseThrow(NotFoundException::new);
//        mapToEntity(communicationBoardDTO, communicationBoard);
        communicationBoard.setTitle(communicationBoardDTO.getTitle());
        communicationBoard.setContent(communicationBoardDTO.getContent());
        communicationBoard.setMaxnum(communicationBoardDTO.getMaxnum());
        communicationBoard.setValid(communicationBoardDTO.getValid());
        if (communicationBoardDTO.getLikeCount() != null) {
            communicationBoard.setLikeCount(communicationBoardDTO.getLikeCount());
        }
        communicationBoardRepository.save(communicationBoard);
    }

    public void delete(final Integer comnId) {
        communicationBoardRepository.deleteById(comnId);
    }

    private CommunicationBoardDTO mapToDTO(final CommunicationBoard communicationBoard,
            final CommunicationBoardDTO communicationBoardDTO) {
        communicationBoardDTO.setComnId(communicationBoard.getComnId());
        communicationBoardDTO.setContent(communicationBoard.getContent());
        communicationBoardDTO.setMaxnum(communicationBoard.getMaxnum());
        communicationBoardDTO.setLikeCount(communicationBoard.getLikeCount());
        communicationBoardDTO.setValid(communicationBoard.getValid());
        communicationBoardDTO.setTitle(communicationBoard.getTitle());
        communicationBoardDTO.setAuthor(communicationBoard.getAuthor() == null ? null : communicationBoard.getAuthor());
        communicationBoardDTO.setLectureId(communicationBoard.getLectureId() == null ? null : communicationBoard.getLectureId());
        return communicationBoardDTO;
    }

    private CommunicationBoard mapToEntity(final CommunicationBoardDTO communicationBoardDTO,
            final CommunicationBoard communicationBoard) {
        communicationBoard.setTitle(communicationBoardDTO.getTitle());
        communicationBoard.setContent(communicationBoardDTO.getContent());
        communicationBoard.setMaxnum(communicationBoardDTO.getMaxnum());
        communicationBoard.setValid(communicationBoardDTO.getValid());
        communicationBoard.setLikeCount(communicationBoardDTO.getLikeCount());
        final User author = communicationBoardDTO.getAuthor() == null ? null : userRepository.findById(communicationBoardDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("author not found"));
        communicationBoard.setAuthor(author);
        final LectureList lectureId = communicationBoardDTO.getLectureId() == null ? null : lectureListRepository.findById(communicationBoardDTO.getLectureId().getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        communicationBoard.setLectureId(lectureId);
        return communicationBoard;
    }

}
