package termproject.studyroom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.*;
import termproject.studyroom.model.SharingBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.SharingCommentRepository;
import termproject.studyroom.repos.SharingFileRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class SharingBoardService {

    private final SharingBoardRepository sharingBoardRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final SharingCommentRepository sharingCommentRepository;
    private final SharingFileRepository sharingFileRepository;

    public SharingBoardService(final SharingBoardRepository sharingBoardRepository,
            final UserRepository userRepository, final LectureListRepository lectureListRepository,
            final SharingCommentRepository sharingCommentRepository,
            final SharingFileRepository sharingFileRepository) {
        this.sharingBoardRepository = sharingBoardRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.sharingCommentRepository = sharingCommentRepository;
        this.sharingFileRepository = sharingFileRepository;
    }

    public List<SharingBoardDTO> findAll() {
        final List<SharingBoard> sharingBoards = sharingBoardRepository.findAll(Sort.by("sharingId"));
        return sharingBoards.stream()
                .map(sharingBoard -> mapToDTO(sharingBoard, new SharingBoardDTO()))
                .toList();
    }

    public SharingBoardDTO get(final Integer sharingId) {
        return sharingBoardRepository.findById(sharingId)
                .map(sharingBoard -> mapToDTO(sharingBoard, new SharingBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Page<SharingBoard> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "sharingId"));
        return this.sharingBoardRepository.findAll(pageable);
    }

    public Integer create(final SharingBoardDTO sharingBoardDTO) {
        final SharingBoard sharingBoard = new SharingBoard();
        mapToEntity(sharingBoardDTO, sharingBoard);
        return sharingBoardRepository.save(sharingBoard).getSharingId();
    }

    public void update(final Integer sharingId, final SharingBoardDTO sharingBoardDTO) {
        final SharingBoard sharingBoard = sharingBoardRepository.findById(sharingId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sharingBoardDTO, sharingBoard);
        sharingBoardRepository.save(sharingBoard);
    }

    public void delete(final Integer sharingId) {
        sharingBoardRepository.deleteById(sharingId);
    }

    private SharingBoardDTO mapToDTO(final SharingBoard sharingBoard,
            final SharingBoardDTO sharingBoardDTO) {
        sharingBoardDTO.setSharingId(sharingBoard.getSharingId());
        sharingBoardDTO.setTitle(sharingBoard.getTitle());
        sharingBoardDTO.setContent(sharingBoard.getContent());
//        sharingBoardDTO.setWarnCount(sharingBoard.getWarnCount());
        sharingBoardDTO.setUserId(sharingBoard.getUserId() == null ? null : sharingBoard.getUserId());
        sharingBoardDTO.setLectureId(sharingBoard.getLectureId() == null ? null : sharingBoard.getLectureId());
        return sharingBoardDTO;
    }

    private SharingBoard mapToEntity(final SharingBoardDTO sharingBoardDTO,
            final SharingBoard sharingBoard) {
        sharingBoard.setTitle(sharingBoardDTO.getTitle());
        sharingBoard.setContent(sharingBoardDTO.getContent());
//        sharingBoard.setWarnCount(sharingBoardDTO.getWarnCount());
        final User userId = sharingBoardDTO.getUserId() == null ? null : userRepository.findById(sharingBoardDTO.getUserId().getStdId())
                .orElseThrow(() -> new NotFoundException("userId not found"));
        sharingBoard.setUserId(userId);
        final LectureList lectureId = sharingBoardDTO.getLectureId() == null ? null : lectureListRepository.findById(sharingBoardDTO.getLectureId().getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        sharingBoard.setLectureId(lectureId);
        return sharingBoard;
    }

    public ReferencedWarning getReferencedWarning(final Integer sharingId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final SharingBoard sharingBoard = sharingBoardRepository.findById(sharingId)
                .orElseThrow(NotFoundException::new);
        final SharingComment shIdSharingComment = sharingCommentRepository.findFirstByShId(sharingBoard);
        if (shIdSharingComment != null) {
            referencedWarning.setKey("sharingBoard.sharingComment.shId.referenced");
            referencedWarning.addParam(shIdSharingComment.getShcomId());
            return referencedWarning;
        }
        final SharingFile shIdSharingFile = sharingFileRepository.findFirstByShId(sharingBoard);
        if (shIdSharingFile != null) {
            referencedWarning.setKey("sharingBoard.sharingFile.shId.referenced");
            referencedWarning.addParam(shIdSharingFile.getShfId());
            return referencedWarning;
        }
        return null;
    }

}
