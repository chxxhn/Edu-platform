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
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.model.SharingFileDTO;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.service.SharingFileService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/sharingFiles")
public class SharingFileController {

    private final SharingFileService sharingFileService;
    private final SharingBoardRepository sharingBoardRepository;

    public SharingFileController(final SharingFileService sharingFileService,
            final SharingBoardRepository sharingBoardRepository) {
        this.sharingFileService = sharingFileService;
        this.sharingBoardRepository = sharingBoardRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("shIdValues", sharingBoardRepository.findAll(Sort.by("sharingId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(SharingBoard::getSharingId, SharingBoard::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("sharingFiles", sharingFileService.findAll());
        return "sharingFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("sharingFile") final SharingFileDTO sharingFileDTO) {
        return "sharingFile/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("sharingFile") @Valid final SharingFileDTO sharingFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sharingFile/add";
        }
        sharingFileService.create(sharingFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingFile.create.success"));
        return "redirect:/sharingFiles";
    }

    @GetMapping("/edit/{shfId}")
    public String edit(@PathVariable(name = "shfId") final Integer shfId, final Model model) {
        model.addAttribute("sharingFile", sharingFileService.get(shfId));
        return "sharingFile/edit";
    }

    @PostMapping("/edit/{shfId}")
    public String edit(@PathVariable(name = "shfId") final Integer shfId,
            @ModelAttribute("sharingFile") @Valid final SharingFileDTO sharingFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sharingFile/edit";
        }
        sharingFileService.update(shfId, sharingFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingFile.update.success"));
        return "redirect:/sharingFiles";
    }

    @PostMapping("/delete/{shfId}")
    public String delete(@PathVariable(name = "shfId") final Integer shfId,
            final RedirectAttributes redirectAttributes) {
        sharingFileService.delete(shfId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("sharingFile.delete.success"));
        return "redirect:/sharingFiles";
    }

}
