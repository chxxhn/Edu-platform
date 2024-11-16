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
import termproject.studyroom.model.OldExamDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.OldExamService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/oldExams")
public class OldExamController {

    private final OldExamService oldExamService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public OldExamController(final OldExamService oldExamService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.oldExamService = oldExamService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("oldExams", oldExamService.findAll());
        return "oldExam/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("oldExam") final OldExamDTO oldExamDTO) {
        return "oldExam/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("oldExam") @Valid final OldExamDTO oldExamDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "oldExam/add";
        }
        oldExamService.create(oldExamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExam.create.success"));
        return "redirect:/oldExams";
    }

    @GetMapping("/edit/{oeId}")
    public String edit(@PathVariable(name = "oeId") final Integer oeId, final Model model) {
        model.addAttribute("oldExam", oldExamService.get(oeId));
        return "oldExam/edit";
    }

    @PostMapping("/edit/{oeId}")
    public String edit(@PathVariable(name = "oeId") final Integer oeId,
            @ModelAttribute("oldExam") @Valid final OldExamDTO oldExamDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "oldExam/edit";
        }
        oldExamService.update(oeId, oldExamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExam.update.success"));
        return "redirect:/oldExams";
    }

    @PostMapping("/delete/{oeId}")
    public String delete(@PathVariable(name = "oeId") final Integer oeId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = oldExamService.getReferencedWarning(oeId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            oldExamService.delete(oeId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("oldExam.delete.success"));
        }
        return "redirect:/oldExams";
    }

}
