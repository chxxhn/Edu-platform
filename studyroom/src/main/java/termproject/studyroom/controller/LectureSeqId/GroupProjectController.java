package termproject.studyroom.controller.LectureSeqId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.*;
import termproject.studyroom.model.*;
import termproject.studyroom.repos.*;
import termproject.studyroom.service.AlarmService;
import termproject.studyroom.service.GroupProjectService;
import termproject.studyroom.service.GroupUserService;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.List;
import java.util.stream.Stream;


@Controller
@RequestMapping("/groupProjects")
public class GroupProjectController {

    private final GroupProjectService groupProjectService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final LectureUserRepository lectureUserRepository;
    private final GroupUserService groupUserService;
    private final GroupApproveRepository groupApproveRepository;
    private final AlarmService alarmService;
    private final UserService userService;

    public GroupProjectController(final GroupProjectService groupProjectService,
                                  final UserRepository userRepository,
                                  final LectureListRepository lectureListRepository,
                                  GroupProjectRepository groupProjectRepository,
                                  GroupUserService groupUserService,
                                  LectureUserRepository lectureUserRepository,
                                  GroupApproveRepository groupApproveRepository,
                                  AlarmService alarmService, UserService userService) {
        this.groupProjectService = groupProjectService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.lectureUserRepository = lectureUserRepository;
        this.groupUserService = groupUserService;
        this.groupApproveRepository = groupApproveRepository;
        this.alarmService = alarmService;
        this.userService = userService;
    }

    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("createdUserIdValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
    }

    @GetMapping("/{lectureId}")
    public String list(
            @PathVariable(name = "lectureId", required = false) Integer lectureId,
            final Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @AuthenticationPrincipal CustomUserDetails user) {

        model.addAttribute("groupProjects", groupProjectService.findAll());

        if (lectureId != null) {
            // 특정 강의 질문 페이징 처리
            LectureList lecture = lectureListRepository.findById(lectureId)
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
            model.addAttribute("selectedLecture", lecture);

            User loginuser = user.getUser();
            String role = loginuser.getGrade() == Grade.PROF || loginuser.getGrade() == Grade.TA ? "admin" : "consumer";
            model.addAttribute("role", role);
            String prof = loginuser.getGrade() == Grade.PROF ? "prof" : "";
            model.addAttribute("prof",prof);
            model.addAttribute("user", loginuser);

            Page<GroupProject> paging;
            if ("consumer".equals(role)) {
                // `group_valid` 값이 1인 것만 페이징 처리
                paging = groupProjectRepository.findByLectureIdAndGroupValidWithPaging(
                        lecture.getLectureId(),true, PageRequest.of(page, 10));
            } else {
                // 모든 항목 페이징 처리
                paging = groupProjectRepository.findByLectureIdWithPaging(lecture, PageRequest.of(page, 10));
            }
            // 승인된 그룹 프로젝트 조회
            List<GroupApprove> groupApproves = groupApproveRepository.findByLectureList(lecture);
            model.addAttribute("groupApproves", groupApproves);
            model.addAttribute("paging", paging);
//            model.addAttribute("questionBoards", groupProjectService.findAll());
        } else {
            return "";
        }
        return "groupProject/list";
    }


    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId
            ,Model model, @AuthenticationPrincipal CustomUserDetails user, @ModelAttribute("groupProject") final GroupProjectDTO groupProjectDTO) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        User loginuser= user.getUser();
        model.addAttribute("user", loginuser);

        List<LectureUser> lectureUsers = null; // 기본값을 null로 설정
        if (loginuser.getGrade() == Grade.TA) {
            lectureUsers = lectureUserRepository.findAvailableLectureUsers(lectureId);
//
        }
        model.addAttribute("lectureUsers", lectureUsers);

        return "groupProject/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId") Integer lectureId,
                      @RequestParam(value = "stdId1", required = false) Integer stdId1,
                      @RequestParam(value = "stdId2", required = false) Integer stdId2,
                      @RequestParam(value = "stdId3", required = false) Integer stdId3,
                      @AuthenticationPrincipal CustomUserDetails user,
                      @ModelAttribute("groupProject") final GroupProjectDTO groupProjectDTO,
                      final BindingResult bindingResult,
                      final RedirectAttributes redirectAttributes,
                      final Model model) {

        try {
            // 강의 ID로 LectureList를 조회
            LectureList lecture = lectureListRepository.findById(lectureId)
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
            // 세션 유저 정보를 조회
            User author = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (groupProjectDTO.getCreatedUserId() == null ) { groupProjectDTO.setCreatedUserId(author.getStdId()); }

// 팀원 ID 리스트 생성
            List<Integer> teamMemberIds = Stream.of(stdId1, stdId2, stdId3)
                    .filter(java.util.Objects::nonNull) // null 값 제외
                    .toList();
            model.addAttribute("selectedLecture", lecture);

            // 서비스 호출
            groupProjectService.createGroupProjectWithMembers(groupProjectDTO, lecture,groupProjectDTO.getCreatedUserId(), teamMemberIds);

            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupProject.create.success"));


            //조교한테 알람 가기.
            //알람 DTO 에 등록
            //조교 리스트 받기
            List<UserDTO> userTA = userService.findGrade(Grade.TA);

            AlarmDTO baseAlarmDTO = new AlarmDTO();
            baseAlarmDTO.setAlarmType(AlarmType.GROUP_REQUEST);
            baseAlarmDTO.setContent(lecture.getName() + "(" + lecture.getLectureId() + ")" + " 강의의 " + groupProjectDTO.getGroupName() + " 그룹 승인이 신청되었습니다.");
            baseAlarmDTO.setLectureId(lectureId);
            baseAlarmDTO.setBoardId(groupProjectDTO.getGpId());
            for (UserDTO ta : userTA) {
                AlarmDTO alarmDTO = new AlarmDTO();
                alarmDTO.setAlarmType(baseAlarmDTO.getAlarmType());
                alarmDTO.setContent(baseAlarmDTO.getContent());
                alarmDTO.setUserId(ta.getStdId()); // 조교의 ID 설정
                alarmDTO.setBoardId(baseAlarmDTO.getBoardId());
                alarmDTO.setLectureId(baseAlarmDTO.getLectureId());
                alarmService.create(alarmDTO);
            }
        } catch (Exception e) {
            // 에러 발생 시 실패 메시지 전달
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("groupProject.create.failed", e.getMessage()));
        }

        return "redirect:/groupProjects/" + lectureId;
    }



    @GetMapping("/edit/{lectureId}/{gpId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId, @PathVariable(name = "gpId") final Integer gpId,  final Model model,
                       @AuthenticationPrincipal CustomUserDetails user ,
                       final RedirectAttributes redirectAttributes
                       ) {
        User currentUser = user.getUser();

        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("groupProject", groupProjectService.get(gpId));
        model.addAttribute("user", currentUser != null ? currentUser : "Anonymous User");
        // 게시물 정보 확인
        GroupProjectDTO groupProject = groupProjectService.get(gpId);
        // TA의 경우 승인 여부 화면으로 이동
        if (currentUser.getGrade() == Grade.TA) {
            model.addAttribute("isTA", true);
            return "groupProject/approve";
        }
        if (groupProject == null || !groupProject.getCreatedUserId().equals(currentUser.getStdId())) {
            // 게시물을 쓴 사람과 현재 사용자가 다를 경우 목록으로 리다이렉트
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("작성자만수정가능합니다."));
            return "redirect:/groupProjects/" + lectureId;
        }
        return "groupProject/edit";
    }

    @PostMapping("/approve/{lectureId}/{gpId}")
    public String approveGroupProject(
            @PathVariable(name = "lectureId") final Integer lectureId,
            @PathVariable(name = "gpId") final Integer gpId,
                                      @AuthenticationPrincipal CustomUserDetails user,
                                      final RedirectAttributes redirectAttributes) {
        User currentUser = user.getUser();

        if (currentUser == null || currentUser.getGrade() != Grade.TA) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("권한이 없습니다."));
            return "redirect:/groupProjects/"+lectureId;
        }

        // groupValid 값 변경
        GroupProjectDTO groupProject = groupProjectService.get(gpId);

        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        if (groupProject != null) {
            groupProject.setGroupValid(true);
            groupProjectService.update(gpId, groupProject);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("승인이 완료되었습니다."));
            //알람 DTO 에 등록
            AlarmDTO alarmDTO = new AlarmDTO();
            alarmDTO.setAlarmType(AlarmType.GROUP_APPROVE);
            alarmDTO.setContent(lecture.getName()+"("+lecture.getLectureId()+")"+"강의의 "+groupProject.getGroupName()+" 그룹 승인이 완료되었습니다.");
            alarmDTO.setBoardId(groupProject.getGpId());
            alarmDTO.setUserId(groupProject.getCreatedUserId());
            alarmDTO.setLectureId(lectureId);
            alarmService.create(alarmDTO);

        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("해당 그룹 프로젝트를 찾을 수 없습니다."));
        }

        return "redirect:/groupProjects/" +lectureId;
    }


        @PostMapping("/edit/{lectureId}/{gpId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
        @PathVariable(name = "gpId") final Integer gpId,
            @ModelAttribute("groupProject") final GroupProjectDTO groupProjectDTO,
            final BindingResult bindingResult,
        final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user) {
            LectureList lecture = lectureListRepository.findById(lectureId)
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

            // 세션 유저 정보를 조회
            User author = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            // DTO에 강의 및 작성자 설정

        groupProjectService.update(gpId, groupProjectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupProject.update.success"));
        return "redirect:/groupProjects/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{gpId}")
    public String delete(
            @PathVariable(name = "lectureId") Integer lectureId
            , @PathVariable(name = "gpId") final Integer gpId,
            final RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails user) {
        final ReferencedWarning referencedWarning = groupProjectService.getReferencedWarning(gpId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            groupProjectService.delete(gpId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupProject.delete.success"));
        }
        return "redirect:/groupProjects/" +lectureId;
    }
}
