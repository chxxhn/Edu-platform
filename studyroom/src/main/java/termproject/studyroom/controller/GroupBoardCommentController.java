package termproject.studyroom.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupBoardCommentDTO;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.GroupBoardCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/groupBoardComments")
public class GroupBoardCommentController {

    private final GroupBoardCommentService groupBoardCommentService;
    private final UserRepository userRepository;
    private final GroupBoardRepository groupBoardRepository;

    public GroupBoardCommentController(final GroupBoardCommentService groupBoardCommentService,
            final UserRepository userRepository, final GroupBoardRepository groupBoardRepository) {
        this.groupBoardCommentService = groupBoardCommentService;
        this.userRepository = userRepository;
        this.groupBoardRepository = groupBoardRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("gbIdValues", groupBoardRepository.findAll(Sort.by("gbId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GroupBoard::getGbId, GroupBoard::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groupBoardComments", groupBoardCommentService.findAll());
        return "groupBoardComment/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("groupBoardComment") final GroupBoardCommentDTO groupBoardCommentDTO) {
        return "groupBoardComment/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("groupBoardComment") @Valid final GroupBoardCommentDTO groupBoardCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoardComment/add";
        }
        groupBoardCommentService.create(groupBoardCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoardComment.create.success"));
        return "redirect:/groupBoardComments";
    }

    @GetMapping("/edit/{gbcomId}")
    public String edit(@PathVariable(name = "gbcomId") final Integer gbcomId, final Model model) {
        model.addAttribute("groupBoardComment", groupBoardCommentService.get(gbcomId));
        return "groupBoardComment/edit";
    }

    @PostMapping("/edit/{gbcomId}")
    public String edit(@PathVariable(name = "gbcomId") final Integer gbcomId,
            @ModelAttribute("groupBoardComment") @Valid final GroupBoardCommentDTO groupBoardCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoardComment/edit";
        }
        groupBoardCommentService.update(gbcomId, groupBoardCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoardComment.update.success"));
        return "redirect:/groupBoardComments";
    }

    @PostMapping("/delete/{gbcomId}")
    public String delete(@PathVariable(name = "gbcomId") final Integer gbcomId,
            final RedirectAttributes redirectAttributes) {
        groupBoardCommentService.delete(gbcomId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupBoardComment.delete.success"));
        return "redirect:/groupBoardComments";
    }

}
