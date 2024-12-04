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
import termproject.studyroom.model.NoticeBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.NoticeBoardService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/noticeBoards")
public class NoticeBoardController {

    private final NoticeBoardService noticeBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public NoticeBoardController(final NoticeBoardService noticeBoardService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.noticeBoardService = noticeBoardService;
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
        model.addAttribute("noticeBoards", noticeBoardService.findAll());
        return "noticeBoard/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("noticeBoard") final NoticeBoardDTO noticeBoardDTO) {
        return "noticeBoard/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("noticeBoard") @Valid final NoticeBoardDTO noticeBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "noticeBoard/add";
        }
        noticeBoardService.create(noticeBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeBoard.create.success"));
        return "redirect:/noticeBoards";
    }

    @GetMapping("/edit/{noticeId}")
    public String edit(@PathVariable(name = "noticeId") final Integer noticeId, final Model model) {
        model.addAttribute("noticeBoard", noticeBoardService.get(noticeId));
        return "noticeBoard/edit";
    }

    @PostMapping("/edit/{noticeId}")
    public String edit(@PathVariable(name = "noticeId") final Integer noticeId,
            @ModelAttribute("noticeBoard") @Valid final NoticeBoardDTO noticeBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "noticeBoard/edit";
        }
        noticeBoardService.update(noticeId, noticeBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeBoard.update.success"));
        return "redirect:/noticeBoards";
    }

    @PostMapping("/delete/{noticeId}")
    public String delete(@PathVariable(name = "noticeId") final Integer noticeId,
            final RedirectAttributes redirectAttributes) {
        noticeBoardService.delete(noticeId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("noticeBoard.delete.success"));
        return "redirect:/noticeBoards";
    }

    @GetMapping(value = "/detail/{noticeId}")
    public String detail(@PathVariable(name = "noticeId") final Integer noticeId, final Model model) {
        NoticeBoardDTO notice = noticeBoardService.get(noticeId);
        model.addAttribute("noticeBoard", notice);
        return "noticeBoard/detail";
    }

}
