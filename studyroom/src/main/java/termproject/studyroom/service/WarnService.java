package termproject.studyroom.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.*;
import termproject.studyroom.model.BoardType;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.model.WarnDTO;
import termproject.studyroom.repos.*;
import termproject.studyroom.util.NotFoundException;


@Service
public class WarnService {

    private final WarnRepository warnRepository;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final LectureRequestRepository lectureRequestRepository;

    public WarnService(final WarnRepository warnRepository,
                       final UserRepository userRepository, QuestionBoardRepository questionBoardRepository, SharingBoardRepository sharingBoardRepository, LectureRequestRepository lectureRequestRepository) {
        this.warnRepository = warnRepository;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.lectureRequestRepository = lectureRequestRepository;
    }

    public Integer addWarn(WarnDTO warnDTO) {
        // User 확인
        User user = userRepository.findById(warnDTO.getAuthor().getStdId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BoardType boardType = warnDTO.getBoardType();

        // 공통 로직: 신고 존재 여부 확인
        Warn existingLike = warnRepository.findByUserAndPostIdAndBoardType(user, warnDTO.getPostId(), boardType);

        if (existingLike != null) {
            // 신고 취소
            warnRepository.delete(existingLike);
            updateWarnCount(warnDTO.getPostId(), boardType, -1);
            return -1; // 신고 취소됨
        } else {
            // 신고 추가
            Warn warn = new Warn();
            warn.setUser(user);
            warn.setPostId(warnDTO.getPostId());
            warn.setBoardType(boardType);
            warnRepository.save(warn);

            updateWarnCount(warnDTO.getPostId(), boardType, 1);
            return 1; // 신고 추가됨
        }
    }

    private void updateWarnCount(Integer postId, BoardType boardType, int increment) {
        switch (boardType) {
            case QUESTION_BOARD -> {
                QuestionBoard questionBoard = questionBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("QuestionBoard not found"));
                questionBoard.setWarnCount(questionBoard.getWarnCount() + increment);
                questionBoardRepository.save(questionBoard);
            }
            case SHARING_BOARD -> {
                SharingBoard sharingBoard = sharingBoardRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("SharingBoard not found"));
                sharingBoard.setWarnCount(sharingBoard.getWarnCount() + increment);
                sharingBoardRepository.save(sharingBoard);
            }
            case LECTURE_REQUEST -> {
                LectureRequest lectureRequest = lectureRequestRepository.findById(postId)
                        .orElseThrow(() -> new IllegalArgumentException("LectureRequest not found"));
                lectureRequest.setWarnCount(lectureRequest.getWarnCount() + increment);
                lectureRequestRepository.save(lectureRequest);
            }

            default -> throw new IllegalArgumentException("Unsupported BoardType");
        }
    }

    public int getWarnCount(Integer postId) {
        return warnRepository.countByPostId(postId);
    }

    // 모든 싫어요 가져오기
    public List<WarnDTO> findAll() {
        final List<Warn> warns = warnRepository.findAll(Sort.by("id"));
        return warns.stream()
                .map(warn -> mapToDTO(warn, new WarnDTO()))
                .toList();
    }

    // 특정 신고 가져오기
    public WarnDTO get(final Integer id) {
        return warnRepository.findById(id)
                .map(warn -> mapToDTO(warn, new WarnDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // 좋아요 추가
    public Integer create(final WarnDTO warnDTO) {
        final Warn warn = new Warn();
        mapToEntity(warnDTO, warn);
        return warnRepository.save(warn).getId();
    }

    // 좋아요 수정 (필요 시)
    public void update(final Integer id, final WarnDTO warnDTO) {
        final Warn warn = warnRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(warnDTO, warn);
        warnRepository.save(warn);
    }

    // 좋아요 삭제
    public void delete(final Integer id) {
        warnRepository.deleteById(id);
    }

    // Like Entity -> LikeDTO 변환
    private WarnDTO mapToDTO(final Warn warn, final WarnDTO warnDTO) {
        warnDTO.setId(warn.getId());
        warnDTO.setAuthor(warn.getUser());
        warnDTO.setPostId(warn.getPostId());
        warnDTO.setBoardType(warn.getBoardType());
//        likeDTO.setDateCreated(like.getDateCreated());
//        likeDTO.setLastUpdated(like.getLastUpdated());
        return warnDTO;
    }


    // LikeDTO -> Like Entity 변환
    private Warn mapToEntity(final WarnDTO warnDTO, final Warn warn) {
        final User user = userRepository.findById(warnDTO.getAuthor().getStdId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        warn.setUser(user);
        warn.setPostId(warnDTO.getPostId());
        warn.setBoardType(warnDTO.getBoardType());
        return warn;
    }
}

