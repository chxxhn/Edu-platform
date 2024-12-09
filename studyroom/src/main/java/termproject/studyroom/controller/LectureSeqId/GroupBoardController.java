package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.GroupProject;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupBoardDTO;
import termproject.studyroom.repos.GroupProjectRepository;
import termproject.studyroom.repos.GroupUserRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.GroupBoardService;
import termproject.studyroom.service.GroupUserService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/groupBoards")
public class GroupBoardController {

    private final GroupBoardService groupBoardService;
    private final UserRepository userRepository;
    private final GroupProjectRepository groupProjectRepository;
    private final GroupUserRepository groupUserRepository;
    private final GroupUserService groupUserService;


    public GroupBoardController(final GroupBoardService groupBoardService,
            final UserRepository userRepository,
            final GroupProjectRepository groupProjectRepository,
                                final GroupUserRepository groupUserRepository,
                                final GroupUserService groupUserService) {
        this.groupBoardService = groupBoardService;
        this.userRepository = userRepository;
        this.groupProjectRepository = groupProjectRepository;
        this.groupUserRepository= groupUserRepository;
        this.groupUserService = groupUserService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("gpIdValues", groupProjectRepository.findAll(Sort.by("gpId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GroupProject::getGpId, GroupProject::getGroupName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groupBoards", groupBoardService.findAll());
        return "groupBoard/list";
    }

    @GetMapping("/add/{lectureId}/{gpId}")
    public String add(@PathVariable("gpId") Integer gpId,
                      @PathVariable("lectureId") Integer lectureId,
                      @ModelAttribute("groupBoard") final GroupBoardDTO groupBoardDTO,
                      Model model,
                      @AuthenticationPrincipal CustomUserDetails user,
                      final RedirectAttributes redirectAttributes) {
        User loginuser = user.getUser();
        boolean exists = groupUserService.isUserInGroup(loginuser.getStdId(), lectureId);
        if (!exists) {
            // "권한 없음" 메시지 설정
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("권한이 없습니다."));
            // 이전 페이지로 리다이렉트
            return "redirect:/groupApproves/detail/" + lectureId + "/" + gpId; // 상세 페이지로 리다이렉트
        }
        model.addAttribute("gpId", gpId);
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("user", loginuser);
        return "groupBoard/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("groupBoard") @Valid final GroupBoardDTO groupBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoard/add";
        }
        groupBoardService.create(groupBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoard.create.success"));
        return "redirect:/groupBoards";
    }

    @GetMapping("/edit/{gbId}")
    public String edit(@PathVariable(name = "gbId") final Integer gbId, final Model model) {
        model.addAttribute("groupBoard", groupBoardService.get(gbId));
        return "groupBoard/edit";
    }

    @PostMapping("/edit/{gbId}")
    public String edit(@PathVariable(name = "gbId") final Integer gbId,
            @ModelAttribute("groupBoard") @Valid final GroupBoardDTO groupBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoard/edit";
        }
        groupBoardService.update(gbId, groupBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoard.update.success"));
        return "redirect:/groupBoards";
    }

    @PostMapping("/delete/{gbId}")
    public String delete(@PathVariable(name = "gbId") final Integer gbId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = groupBoardService.getReferencedWarning(gbId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            groupBoardService.delete(gbId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupBoard.delete.success"));
        }
        return "redirect:/groupBoards";
    }

}
