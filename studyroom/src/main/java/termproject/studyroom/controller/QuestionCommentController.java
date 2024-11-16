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
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/questionComments")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;

    public QuestionCommentController(final QuestionCommentService questionCommentService,
            final UserRepository userRepository,
            final QuestionBoardRepository questionBoardRepository) {
        this.questionCommentService = questionCommentService;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("qIdValues", questionBoardRepository.findAll(Sort.by("questionId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(QuestionBoard::getQuestionId, QuestionBoard::getTitle)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("questionComments", questionCommentService.findAll());
        return "questionComment/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("questionComment") final QuestionCommentDTO questionCommentDTO) {
        return "questionComment/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("questionComment") @Valid final QuestionCommentDTO questionCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionComment/add";
        }
        questionCommentService.create(questionCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionComment.create.success"));
        return "redirect:/questionComments";
    }

    @GetMapping("/edit/{qcomId}")
    public String edit(@PathVariable(name = "qcomId") final Integer qcomId, final Model model) {
        model.addAttribute("questionComment", questionCommentService.get(qcomId));
        return "questionComment/edit";
    }

    @PostMapping("/edit/{qcomId}")
    public String edit(@PathVariable(name = "qcomId") final Integer qcomId,
            @ModelAttribute("questionComment") @Valid final QuestionCommentDTO questionCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionComment/edit";
        }
        questionCommentService.update(qcomId, questionCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionComment.update.success"));
        return "redirect:/questionComments";
    }

    @PostMapping("/delete/{qcomId}")
    public String delete(@PathVariable(name = "qcomId") final Integer qcomId,
            final RedirectAttributes redirectAttributes) {
        questionCommentService.delete(qcomId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("questionComment.delete.success"));
        return "redirect:/questionComments";
    }

}
