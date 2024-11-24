package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.Alarm;
import termproject.studyroom.domain.CommunicationBoard;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.GroupBoardComment;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.SharingComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.repos.AlarmRepository;
import termproject.studyroom.repos.CommunicationBoardRepository;
import termproject.studyroom.repos.GroupBoardCommentRepository;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.repos.GroupProjectRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.NoticeBoardRepository;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.QuestionCommentRepository;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.SharingCommentRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final GroupBoardCommentRepository groupBoardCommentRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final QuestionCommentRepository questionCommentRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final SharingCommentRepository sharingCommentRepository;
    private final OldExamRepository oldExamRepository;
    private final CommunicationBoardRepository communicationBoardRepository;
    private final AlarmRepository alarmRepository;

    public UserService(final UserRepository userRepository,
            final LectureListRepository lectureListRepository,
            final GroupProjectRepository groupProjectRepository,
            final GroupBoardRepository groupBoardRepository,
            final GroupBoardCommentRepository groupBoardCommentRepository,
            final NoticeBoardRepository noticeBoardRepository,
            final QuestionBoardRepository questionBoardRepository,
            final QuestionCommentRepository questionCommentRepository,
            final SharingBoardRepository sharingBoardRepository,
            final SharingCommentRepository sharingCommentRepository,
            final OldExamRepository oldExamRepository,
            final CommunicationBoardRepository communicationBoardRepository,
            final AlarmRepository alarmRepository) {
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.groupBoardRepository = groupBoardRepository;
        this.groupBoardCommentRepository = groupBoardCommentRepository;
        this.noticeBoardRepository = noticeBoardRepository;
        this.questionBoardRepository = questionBoardRepository;
        this.questionCommentRepository = questionCommentRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.sharingCommentRepository = sharingCommentRepository;
        this.oldExamRepository = oldExamRepository;
        this.communicationBoardRepository = communicationBoardRepository;
        this.alarmRepository = alarmRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("stdId"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Integer stdId) {
        return userRepository.findById(stdId)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getStdId();
    }

    public void update(final Integer stdId, final UserDTO userDTO) {
        final User user = userRepository.findById(stdId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Integer stdId) {
        userRepository.deleteById(stdId);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setStdNum(user.getStdNum());
        userDTO.setStdId(user.getStdId());
        userDTO.setName(user.getName());
        userDTO.setNickname(user.getNickname());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setGrade(user.getGrade());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setStdNum(userDTO.getStdNum());
        user.setName(userDTO.getName());
        user.setNickname(userDTO.getNickname());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setGrade(userDTO.getGrade());
        return user;
    }

    public boolean nicknameExists(final String nickname) {
        return userRepository.existsByNicknameIgnoreCase(nickname);
    }

    public boolean emailExists(final String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final Integer stdId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final User user = userRepository.findById(stdId)
                .orElseThrow(NotFoundException::new);
        final LectureList stdIdLectureList = lectureListRepository.findFirstByStdId(user);
        if (stdIdLectureList != null) {
            referencedWarning.setKey("user.lectureList.stdId.referenced");
            referencedWarning.addParam(stdIdLectureList.getLectureId());
            return referencedWarning;
        }
        final GroupProject createdUserIdGroupProject = groupProjectRepository.findFirstByCreatedUserId(user);
        if (createdUserIdGroupProject != null) {
            referencedWarning.setKey("user.groupProject.createdUserId.referenced");
            referencedWarning.addParam(createdUserIdGroupProject.getGpId());
            return referencedWarning;
        }
        final GroupBoard authorGroupBoard = groupBoardRepository.findFirstByAuthor(user);
        if (authorGroupBoard != null) {
            referencedWarning.setKey("user.groupBoard.author.referenced");
            referencedWarning.addParam(authorGroupBoard.getGbId());
            return referencedWarning;
        }
        final GroupBoardComment authorGroupBoardComment = groupBoardCommentRepository.findFirstByAuthor(user);
        if (authorGroupBoardComment != null) {
            referencedWarning.setKey("user.groupBoardComment.author.referenced");
            referencedWarning.addParam(authorGroupBoardComment.getGbcomId());
            return referencedWarning;
        }
        final NoticeBoard authorNoticeBoard = noticeBoardRepository.findFirstByAuthor(user);
        if (authorNoticeBoard != null) {
            referencedWarning.setKey("user.noticeBoard.author.referenced");
            referencedWarning.addParam(authorNoticeBoard.getNoticeId());
            return referencedWarning;
        }
        final QuestionBoard authorQuestionBoard = questionBoardRepository.findFirstByAuthor(user);
        if (authorQuestionBoard != null) {
            referencedWarning.setKey("user.questionBoard.author.referenced");
            referencedWarning.addParam(authorQuestionBoard.getQuestionId());
            return referencedWarning;
        }
        final QuestionComment authorQuestionComment = questionCommentRepository.findFirstByAuthor(user);
        if (authorQuestionComment != null) {
            referencedWarning.setKey("user.questionComment.author.referenced");
            referencedWarning.addParam(authorQuestionComment.getQcomId());
            return referencedWarning;
        }
        final SharingBoard userIdSharingBoard = sharingBoardRepository.findFirstByUserId(user);
        if (userIdSharingBoard != null) {
            referencedWarning.setKey("user.sharingBoard.userId.referenced");
            referencedWarning.addParam(userIdSharingBoard.getSharingId());
            return referencedWarning;
        }
        final SharingComment authorSharingComment = sharingCommentRepository.findFirstByAuthor(user);
        if (authorSharingComment != null) {
            referencedWarning.setKey("user.sharingComment.author.referenced");
            referencedWarning.addParam(authorSharingComment.getShcomId());
            return referencedWarning;
        }
        final OldExam authorOldExam = oldExamRepository.findFirstByAuthor(user);
        if (authorOldExam != null) {
            referencedWarning.setKey("user.oldExam.author.referenced");
            referencedWarning.addParam(authorOldExam.getOeId());
            return referencedWarning;
        }
        final CommunicationBoard authorCommunicationBoard = communicationBoardRepository.findFirstByAuthor(user);
        if (authorCommunicationBoard != null) {
            referencedWarning.setKey("user.communicationBoard.author.referenced");
            referencedWarning.addParam(authorCommunicationBoard.getComnId());
            return referencedWarning;
        }
        final Alarm userIdAlarm = alarmRepository.findFirstByUserId(user);
        if (userIdAlarm != null) {
            referencedWarning.setKey("user.alarm.userId.referenced");
            referencedWarning.addParam(userIdAlarm.getAlarmId());
            return referencedWarning;
        }
        return null;
    }

}
