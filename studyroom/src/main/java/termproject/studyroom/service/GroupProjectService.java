package termproject.studyroom.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.*;
import termproject.studyroom.model.GroupProjectDTO;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.repos.GroupProjectRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;



@Service
public class GroupProjectService {

    private final GroupProjectRepository groupProjectRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final GroupUserService groupUserService;

    public GroupProjectService(final GroupProjectRepository groupProjectRepository,
            final UserRepository userRepository, final LectureListRepository lectureListRepository,
            final GroupBoardRepository groupBoardRepository,
                               final  GroupUserService groupUserService) {
        this.groupProjectRepository = groupProjectRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.groupBoardRepository = groupBoardRepository;
        this.groupUserService = groupUserService;
    }



    public List<GroupProjectDTO> findAll() {
        final List<GroupProject> groupProjects = groupProjectRepository.findAll(Sort.by("gpId"));
        return groupProjects.stream()
                .map(groupProject -> mapToDTO(groupProject, new GroupProjectDTO()))
                .toList();
    }

    public GroupProjectDTO get(final Integer gpId) {
        return groupProjectRepository.findById(gpId)
                .map(groupProject -> mapToDTO(groupProject, new GroupProjectDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GroupProjectDTO groupProjectDTO) {
        final GroupProject groupProject = new GroupProject();
        mapToEntity(groupProjectDTO, groupProject);
        return groupProjectRepository.save(groupProject).getGpId();
    }

    public GroupProject saveCreate(final GroupProjectDTO groupProjectDTO) {
        final GroupProject groupProject = new GroupProject();
        mapToEntity(groupProjectDTO, groupProject);
        return groupProjectRepository.save(groupProject);
    }

//    public void update(final Integer gpId, final GroupProjectDTO groupProjectDTO) {
//        final GroupProject groupProject = groupProjectRepository.findById(gpId)
//                .orElseThrow(NotFoundException::new);
//        mapToEntity(groupProjectDTO, groupProject);
//        groupProjectRepository.save(groupProject);
//    }

    public void update(final Integer gpId, final GroupProjectDTO groupProjectDTO) {
        final GroupProject groupProject = groupProjectRepository.findById(gpId)
                .orElseThrow(NotFoundException::new);

        // GroupUser 관계 유지
        List<GroupUser> existingUsers = groupProject.getGroupUsers();
        mapToEntity(groupProjectDTO, groupProject);

        // 관계 복원
        groupProject.setGroupUsers(existingUsers);

        groupProjectRepository.save(groupProject);
    }

    public void delete(final Integer gpId) {
        groupProjectRepository.deleteById(gpId);
    }

    private GroupProjectDTO mapToDTO(final GroupProject groupProject,
            final GroupProjectDTO groupProjectDTO) {
        groupProjectDTO.setGpId(groupProject.getGpId());
        groupProjectDTO.setGroupValid(groupProject.getGroupValid());
        groupProjectDTO.setGroupName(groupProject.getGroupName());
        groupProjectDTO.setGroupDetail(groupProject.getGroupDetail());
        groupProjectDTO.setCreatedUserId(groupProject.getCreatedUserId() == null ? null : groupProject.getCreatedUserId().getStdId());
        groupProjectDTO.setLectureId(groupProject.getLectureId() == null ? null : groupProject.getLectureId().getLectureId());
        return groupProjectDTO;
    }

    private GroupProject mapToEntity(final GroupProjectDTO groupProjectDTO,
            final GroupProject groupProject) {
        groupProject.setGroupValid(groupProjectDTO.getGroupValid());
        groupProject.setGroupName(groupProjectDTO.getGroupName());
        groupProject.setGroupDetail(groupProjectDTO.getGroupDetail());
        final User createdUserId = groupProjectDTO.getCreatedUserId() == null ? null : userRepository.findById(groupProjectDTO.getCreatedUserId())
                .orElseThrow(() -> new NotFoundException("createdUserId not found"));
        groupProject.setCreatedUserId(createdUserId);
        final LectureList lectureId = groupProjectDTO.getLectureId() == null ? null : lectureListRepository.findById(groupProjectDTO.getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        groupProject.setLectureId(lectureId);
        return groupProject;
    }

    public boolean groupNameExists(final String groupName) {
        return groupProjectRepository.existsByGroupNameIgnoreCase(groupName);
    }

    public ReferencedWarning getReferencedWarning(final Integer gpId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final GroupProject groupProject = groupProjectRepository.findById(gpId)
                .orElseThrow(NotFoundException::new);
        final GroupBoard gpIdGroupBoard = groupBoardRepository.findFirstByGpId(groupProject);
        if (gpIdGroupBoard != null) {
            referencedWarning.setKey("groupProject.groupBoard.gpId.referenced");
            referencedWarning.addParam(gpIdGroupBoard.getGbId());
            return referencedWarning;
        }
        return null;
    }


    @Transactional
    public void createGroupProjectWithMembers(GroupProjectDTO groupProjectDTO, LectureList lecture,
                                              Integer teamLeaderId, List<Integer> teamMemberIds) {
        // GroupProject 생성
        GroupProject savedGroupProject = saveCreate(groupProjectDTO);

        // Team Members 추가
        groupUserService.addTeamMembers(savedGroupProject, lecture, teamLeaderId, teamMemberIds);
    }


}
