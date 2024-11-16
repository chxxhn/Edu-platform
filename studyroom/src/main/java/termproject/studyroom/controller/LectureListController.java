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
import termproject.studyroom.domain.User;
import termproject.studyroom.model.LectureListDTO;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.LectureListService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/lectureLists")
public class LectureListController {

    private final LectureListService lectureListService;
    private final UserRepository userRepository;

    public LectureListController(final LectureListService lectureListService,
            final UserRepository userRepository) {
        this.lectureListService = lectureListService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("stdIdValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("lectureLists", lectureListService.findAll());
        return "lectureList/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("lectureList") final LectureListDTO lectureListDTO) {
        return "lectureList/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("lectureList") @Valid final LectureListDTO lectureListDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureList/add";
        }
        lectureListService.create(lectureListDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureList.create.success"));
        return "redirect:/lectureLists";
    }

    @GetMapping("/edit/{lectureId}")
    public String edit(@PathVariable(name = "lectureId") final Integer lectureId,
            final Model model) {
        model.addAttribute("lectureList", lectureListService.get(lectureId));
        return "lectureList/edit";
    }

    @PostMapping("/edit/{lectureId}")
    public String edit(@PathVariable(name = "lectureId") final Integer lectureId,
            @ModelAttribute("lectureList") @Valid final LectureListDTO lectureListDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureList/edit";
        }
        lectureListService.update(lectureId, lectureListDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureList.update.success"));
        return "redirect:/lectureLists";
    }

    @PostMapping("/delete/{lectureId}")
    public String delete(@PathVariable(name = "lectureId") final Integer lectureId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = lectureListService.getReferencedWarning(lectureId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            lectureListService.delete(lectureId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lectureList.delete.success"));
        }
        return "redirect:/lectureLists";
    }

}
