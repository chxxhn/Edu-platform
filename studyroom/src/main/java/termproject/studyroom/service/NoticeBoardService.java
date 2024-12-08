package termproject.studyroom.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.NoticeBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.NoticeBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;


@Service
public class NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public NoticeBoardService(final NoticeBoardRepository noticeBoardRepository,
                              final UserRepository userRepository,
                              final LectureListRepository lectureListRepository) {
        this.noticeBoardRepository = noticeBoardRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }

    public List<NoticeBoardDTO> findAll() {
        final List<NoticeBoard> noticeBoards = noticeBoardRepository.findAll(Sort.by("noticeId"));
        return noticeBoards.stream()
                .map(noticeBoard -> mapToDTO(noticeBoard, new NoticeBoardDTO()))
                .toList();
    }

    public NoticeBoardDTO get(final Integer noticeId) {
        return noticeBoardRepository.findById(noticeId)
                .map(noticeBoard -> mapToDTO(noticeBoard, new NoticeBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Page<NoticeBoard> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "noticeId"));
        return this.noticeBoardRepository.findAll(pageable);
    }

    public Integer create(final NoticeBoardDTO noticeBoardDTO) {
        final NoticeBoard noticeBoard = new NoticeBoard();
        mapToEntity(noticeBoardDTO, noticeBoard);
        return noticeBoardRepository.save(noticeBoard).getNoticeId();
    }

    public void update(final Integer noticeId, final NoticeBoardDTO noticeBoardDTO) {
        final NoticeBoard noticeBoard = noticeBoardRepository.findById(noticeId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(noticeBoardDTO, noticeBoard);
        noticeBoardRepository.save(noticeBoard);
    }

    public void delete(final Integer noticeId) {
        noticeBoardRepository.deleteById(noticeId);
    }

    private NoticeBoardDTO mapToDTO(final NoticeBoard noticeBoard,
                                    final NoticeBoardDTO noticeBoardDTO) {
        noticeBoardDTO.setNoticeId(noticeBoard.getNoticeId());
        noticeBoardDTO.setTitle(noticeBoard.getTitle());
        noticeBoardDTO.setContent(noticeBoard.getContent());
        noticeBoardDTO.setAuthor(noticeBoard.getAuthor() == null ? null : noticeBoard.getAuthor());
        noticeBoardDTO.setLectureId(noticeBoard.getLectureId() == null ? null : noticeBoard.getLectureId());
//        noticeBoardDTO.setDateCreated(noticeBoard.getDateCreated().toLocalDateTime());
        return noticeBoardDTO;
    }

    private NoticeBoard mapToEntity(final NoticeBoardDTO noticeBoardDTO,
                                    final NoticeBoard noticeBoard) {
        noticeBoard.setTitle(noticeBoardDTO.getTitle());
        noticeBoard.setContent(noticeBoardDTO.getContent());
        final User author = noticeBoardDTO.getAuthor() == null ? null : userRepository.findById(noticeBoardDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("author not found"));
        noticeBoard.setAuthor(author);
        final LectureList lectureId = noticeBoardDTO.getLectureId() == null ? null : lectureListRepository.findById(noticeBoardDTO.getLectureId().getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        noticeBoard.setLectureId(lectureId);
        return noticeBoard;
    }

}