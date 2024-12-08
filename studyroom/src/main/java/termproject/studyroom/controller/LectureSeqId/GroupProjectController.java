package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
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
import termproject.studyroom.service.GroupProjectService;
import termproject.studyroom.service.GroupUserService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.List;


@Controller
@RequestMapping("/groupProjects")
public class GroupProjectController {

    private final GroupProjectService groupProjectService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final GroupUserService groupUserService;

    public GroupProjectController(final GroupProjectService groupProjectService,
                                  final UserRepository userRepository,
                                  final LectureListRepository lectureListRepository,
                                  GroupProjectRepository groupProjectRepository,
                                  GroupUserService groupUserService,
                                  LectureUserRepository lectureUserRepository) {
        this.groupProjectService = groupProjectService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.lectureUserRepository = lectureUserRepository;
        this.groupUserService = groupUserService;
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
        model.addAttribute("user", user.getUser());

        // 팀원 목록 조회 (수강생 목록)
        List<LectureUser> lectureUsers = lectureUserRepository.findByLectureId(lectureId);
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

            groupProjectDTO.setCreatedUserId(author.getStdId());

            // 팀원 ID 리스트 생성
            List<Integer> teamMemberIds = List.of(stdId1, stdId2, stdId3).stream()
                    .filter(java.util.Objects::nonNull) // null 값 제외
                    .toList();
            model.addAttribute("selectedLecture", lecture);

            // 서비스 호출
            groupProjectService.createGroupProjectWithMembers(groupProjectDTO, lecture, author.getStdId(), teamMemberIds);

            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupProject.create.success"));
        } catch (Exception e) {
            // 에러 발생 시 실패 메시지 전달
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("groupProject.create.failed", e.getMessage()));
        }

        return "redirect:/groupProjects/" + lectureId;
    }


    @GetMapping("/edit/{gpId}")
    public String edit(@PathVariable(name = "gpId") final Integer gpId, final Model model) {
        model.addAttribute("groupProject", groupProjectService.get(gpId));
        return "groupProject/edit";
    }

    @PostMapping("/edit/{gpId}")
    public String edit(@PathVariable(name = "gpId") final Integer gpId,
            @ModelAttribute("groupProject") @Valid final GroupProjectDTO groupProjectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupProject/edit";
        }
        groupProjectService.update(gpId, groupProjectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupProject.update.success"));
        return "redirect:/groupProjects";
    }

    @PostMapping("/delete/{gpId}")
    public String delete(@PathVariable(name = "gpId") final Integer gpId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = groupProjectService.getReferencedWarning(gpId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            groupProjectService.delete(gpId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupProject.delete.success"));
        }
        return "redirect:/groupProjects";
    }

    private final LectureUserRepository lectureUserRepository;
}
