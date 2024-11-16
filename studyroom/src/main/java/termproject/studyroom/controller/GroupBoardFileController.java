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
import termproject.studyroom.model.GroupBoardFileDTO;
import termproject.studyroom.repos.GroupBoardRepository;
import termproject.studyroom.service.GroupBoardFileService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/groupBoardFiles")
public class GroupBoardFileController {

    private final GroupBoardFileService groupBoardFileService;
    private final GroupBoardRepository groupBoardRepository;

    public GroupBoardFileController(final GroupBoardFileService groupBoardFileService,
            final GroupBoardRepository groupBoardRepository) {
        this.groupBoardFileService = groupBoardFileService;
        this.groupBoardRepository = groupBoardRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("gbIdValues", groupBoardRepository.findAll(Sort.by("gbId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GroupBoard::getGbId, GroupBoard::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groupBoardFiles", groupBoardFileService.findAll());
        return "groupBoardFile/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("groupBoardFile") final GroupBoardFileDTO groupBoardFileDTO) {
        return "groupBoardFile/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("groupBoardFile") @Valid final GroupBoardFileDTO groupBoardFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoardFile/add";
        }
        groupBoardFileService.create(groupBoardFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoardFile.create.success"));
        return "redirect:/groupBoardFiles";
    }

    @GetMapping("/edit/{gbfId}")
    public String edit(@PathVariable(name = "gbfId") final Integer gbfId, final Model model) {
        model.addAttribute("groupBoardFile", groupBoardFileService.get(gbfId));
        return "groupBoardFile/edit";
    }

    @PostMapping("/edit/{gbfId}")
    public String edit(@PathVariable(name = "gbfId") final Integer gbfId,
            @ModelAttribute("groupBoardFile") @Valid final GroupBoardFileDTO groupBoardFileDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupBoardFile/edit";
        }
        groupBoardFileService.update(gbfId, groupBoardFileDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupBoardFile.update.success"));
        return "redirect:/groupBoardFiles";
    }

    @PostMapping("/delete/{gbfId}")
    public String delete(@PathVariable(name = "gbfId") final Integer gbfId,
            final RedirectAttributes redirectAttributes) {
        groupBoardFileService.delete(gbfId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupBoardFile.delete.success"));
        return "redirect:/groupBoardFiles";
    }

}
