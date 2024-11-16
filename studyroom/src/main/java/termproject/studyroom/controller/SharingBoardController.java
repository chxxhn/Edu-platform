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
import termproject.studyroom.model.SharingBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.SharingBoardService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/sharingBoards")
public class SharingBoardController {

    private final SharingBoardService sharingBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public SharingBoardController(final SharingBoardService sharingBoardService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.sharingBoardService = sharingBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userIdValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("sharingBoards", sharingBoardService.findAll());
        return "sharingBoard/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("sharingBoard") final SharingBoardDTO sharingBoardDTO) {
        return "sharingBoard/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("sharingBoard") @Valid final SharingBoardDTO sharingBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sharingBoard/add";
        }
        sharingBoardService.create(sharingBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingBoard.create.success"));
        return "redirect:/sharingBoards";
    }

    @GetMapping("/edit/{sharingId}")
    public String edit(@PathVariable(name = "sharingId") final Integer sharingId,
            final Model model) {
        model.addAttribute("sharingBoard", sharingBoardService.get(sharingId));
        return "sharingBoard/edit";
    }

    @PostMapping("/edit/{sharingId}")
    public String edit(@PathVariable(name = "sharingId") final Integer sharingId,
            @ModelAttribute("sharingBoard") @Valid final SharingBoardDTO sharingBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sharingBoard/edit";
        }
        sharingBoardService.update(sharingId, sharingBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingBoard.update.success"));
        return "redirect:/sharingBoards";
    }

    @PostMapping("/delete/{sharingId}")
    public String delete(@PathVariable(name = "sharingId") final Integer sharingId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = sharingBoardService.getReferencedWarning(sharingId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            sharingBoardService.delete(sharingId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("sharingBoard.delete.success"));
        }
        return "redirect:/sharingBoards";
    }

}
