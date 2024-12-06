package termproject.studyroom.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.NotFoundException;
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

    @PostMapping("/add/{questionId}")
    public String add(@PathVariable(name = "questionId") final Integer questionId, @RequestParam(value="content") String content,
                      final RedirectAttributes redirectAttributes) {
        QuestionCommentDTO questionCommentDTO = new QuestionCommentDTO();

        // 테스트용 user 설정, 나중에 로그인 완성하면 고쳐야함
        User user = userRepository.findById(0) // 1번 ID 사용
                .orElseThrow(() -> new IllegalArgumentException("Test user not found"));

        questionCommentDTO.setContent(content);
        questionCommentDTO.setAuthor(user);
        questionCommentDTO.setQId(questionId);
        questionCommentService.create(questionCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionComment.create.success"));
        return "redirect:/questionBoards/detail/" + questionId;
    }

    @GetMapping("/edit/{qcomId}")
    public String edit(@PathVariable(name = "qcomId") final Integer qcomId, final Model model) {
        QuestionCommentDTO comment = questionCommentService.get(qcomId);
        Integer questionId = comment.getQId();
        System.out.println("good");
        comment.setEditing(true); // 수정 상태로 변경
        System.out.println("good");
        model.addAttribute("questionComment", comment);
        return "redirect:/questionBoards/detail/" + questionId;
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
        QuestionCommentDTO comment = questionCommentService.get(qcomId);
        Integer questionId = comment.getQId();
        questionCommentService.delete(qcomId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("questionComment.delete.success"));
        return "redirect:/questionBoards/detail/" + questionId;
    }

}
