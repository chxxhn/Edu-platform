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
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.GroupProjectDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.GroupProjectService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/groupProjects")
public class GroupProjectController {

    private final GroupProjectService groupProjectService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public GroupProjectController(final GroupProjectService groupProjectService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.groupProjectService = groupProjectService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
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

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groupProjects", groupProjectService.findAll());
        return "groupProject/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("groupProject") final GroupProjectDTO groupProjectDTO) {
        return "groupProject/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("groupProject") @Valid final GroupProjectDTO groupProjectDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupProject/add";
        }
        groupProjectService.create(groupProjectDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupProject.create.success"));
        return "redirect:/groupProjects";
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

}
