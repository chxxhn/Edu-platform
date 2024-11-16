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
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionBoardService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/questionBoards")
public class QuestionBoardController {

    private final QuestionBoardService questionBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public QuestionBoardController(final QuestionBoardService questionBoardService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.questionBoardService = questionBoardService;
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
        model.addAttribute("questionBoards", questionBoardService.findAll());
        return "questionBoard/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("questionBoard") final QuestionBoardDTO questionBoardDTO) {
        return "questionBoard/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("questionBoard") @Valid final QuestionBoardDTO questionBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionBoard/add";
        }
        questionBoardService.create(questionBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionBoard.create.success"));
        return "redirect:/questionBoards";
    }

    @GetMapping("/edit/{questionId}")
    public String edit(@PathVariable(name = "questionId") final Integer questionId,
            final Model model) {
        model.addAttribute("questionBoard", questionBoardService.get(questionId));
        return "questionBoard/edit";
    }

    @PostMapping("/edit/{questionId}")
    public String edit(@PathVariable(name = "questionId") final Integer questionId,
            @ModelAttribute("questionBoard") @Valid final QuestionBoardDTO questionBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionBoard/edit";
        }
        questionBoardService.update(questionId, questionBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionBoard.update.success"));
        return "redirect:/questionBoards";
    }

    @PostMapping("/delete/{questionId}")
    public String delete(@PathVariable(name = "questionId") final Integer questionId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = questionBoardService.getReferencedWarning(questionId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            questionBoardService.delete(questionId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("questionBoard.delete.success"));
        }
        return "redirect:/questionBoards";
    }

}
