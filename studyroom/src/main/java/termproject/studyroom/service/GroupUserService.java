package termproject.studyroom.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.*;
import termproject.studyroom.repos.GroupUserRepository;
import termproject.studyroom.repos.LectureUserRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.model.Team;
import termproject.studyroom.util.WebUtils;

import java.util.List;

@Service
public class GroupUserService {

    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final LectureUserRepository lectureUserRepository;

    public GroupUserService(GroupUserRepository groupUserRepository, UserRepository userRepository,
                            LectureUserRepository lectureUserRepository) {
        this.groupUserRepository = groupUserRepository;
        this.userRepository = userRepository;
        this.lectureUserRepository = lectureUserRepository;
    }


    @Transactional(readOnly = true)
    public boolean isUserInGroup(Integer userId, Integer gpId) {
        return groupUserRepository.existsByUserIdAndLectureId(userId, gpId);
    }

    @Transactional
    public void addTeamMembers(GroupProject groupProject, LectureList lecture,
                               Integer teamLeaderId, List<Integer> teamMemberIds) {

        Integer lectureId = lecture.getLectureId();
        List<LectureUser> lectureUsers = lectureUserRepository.findAvailableLectureUsers(lecture.getLectureId());
        // 팀 리더 저장
        User teamLeader = userRepository.findByStdId(teamLeaderId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with stdId: " + teamLeaderId));
        GroupUser teamLeaderEntry = new GroupUser();
        teamLeaderEntry.setGroupUserId(new GroupUserId(teamLeader.getStdId(), lecture.getLectureId()));
        teamLeaderEntry.setUser(teamLeader);
        teamLeaderEntry.setLectureList(lecture);
        teamLeaderEntry.setGroupProject(groupProject);
        teamLeaderEntry.setTeam(Team.TEAMLEADER);
        groupUserRepository.save(teamLeaderEntry);

        // 팀원 저장
        for (Integer stdId : teamMemberIds) {
            boolean isAvailable = lectureUserRepository.isUserAvailableForLecture(lectureId, stdId);
            if (!isAvailable) {
                throw new IllegalArgumentException("Student with stdId: " + stdId + " is not available for this lecture.");
            }
            User teamMember = userRepository.findByStdId(stdId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found with stdId: " + stdId));
            GroupUser teamMemberEntry = new GroupUser();
            teamMemberEntry.setGroupUserId(new GroupUserId(teamMember.getStdId(), lecture.getLectureId()));
            teamMemberEntry.setUser(teamMember);
            teamMemberEntry.setLectureList(lecture);
            teamMemberEntry.setGroupProject(groupProject);
            teamMemberEntry.setTeam(Team.TEAMMEMBER);
            groupUserRepository.save(teamMemberEntry);
        }
    }



}
