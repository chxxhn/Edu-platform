package termproject.studyroom.controller.LectureSeqId;

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
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.model.OldExamFileDTO;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.service.OldExamFileService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/oldExamFiles")
public class OldExamFileController {

    private final OldExamFileService oldExamFileService;
    private final OldExamRepository oldExamRepository;

    public OldExamFileController(final OldExamFileService oldExamFileService,
            final OldExamRepository oldExamRepository) {
        this.oldExamFileService = oldExamFileService;
        this.oldExamRepository = oldExamRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("oeIdValues", oldExamRepository.findAll(Sort.by("oeId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(OldExam::getOeId, OldExam::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("oldExamFiles", oldExamFileService.findAll());
        return "oldExamFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("oldExamFile") final OldExamFileDTO oldExamFileDTO) {
        return "oldExamFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("oldExamFile") @Valid final OldExamFileDTO oldExamFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "oldExamFile/add";
        }
        oldExamFileService.create(oldExamFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExamFile.create.success"));
        return "redirect:/oldExamFiles";
    }

    @GetMapping("/edit/{oefId}")
    public String edit(@PathVariable(name = "oefId") final Integer oefId, final Model model) {
        model.addAttribute("oldExamFile", oldExamFileService.get(oefId));
        return "oldExamFile/edit";
    }

    @PostMapping("/edit/{oefId}")
    public String edit(@PathVariable(name = "oefId") final Integer oefId,
            @ModelAttribute("oldExamFile") @Valid final OldExamFileDTO oldExamFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "oldExamFile/edit";
        }
        oldExamFileService.update(oefId, oldExamFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExamFile.update.success"));
        return "redirect:/oldExamFiles";
    }

    @PostMapping("/delete/{oefId}")
    public String delete(@PathVariable(name = "oefId") final Integer oefId,
            final RedirectAttributes redirectAttributes) {
        oldExamFileService.delete(oefId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("oldExamFile.delete.success"));
        return "redirect:/oldExamFiles";
    }

}
