package termproject.studyroom.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.NoticeBoardDTO;
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionBoardService;
import termproject.studyroom.service.QuestionCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/questionBoards")
public class QuestionBoardController {

    private final QuestionBoardService questionBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final QuestionCommentService questionCommentService;


    public QuestionBoardController(final QuestionBoardService questionBoardService,
                                   final UserRepository userRepository,
                                   final LectureListRepository lectureListRepository, QuestionCommentService questionCommentService) {
        this.questionBoardService = questionBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.questionCommentService = questionCommentService;
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
    public String list(final Model model, @RequestParam(value="page", defaultValue="0") int page) {
        List<QuestionBoardDTO> questionBoards = questionBoardService.findAll();
        Page<QuestionBoard> paging = this.questionBoardService.getList(page);
        model.addAttribute("paging", paging);
        model.addAttribute("questionBoards", questionBoards);
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

    @GetMapping(value = "/detail/{questionId}")
    public String detail(@PathVariable(name = "questionId") final Integer questionId, final Model model) {
        QuestionBoardDTO question = questionBoardService.get(questionId);
        List<QuestionCommentDTO> comments = questionCommentService.findByQuestionId(questionId);
        model.addAttribute("questionBoard", question);
        model.addAttribute("questionComment", comments);
        return "questionBoard/detail";
    }

    @PostMapping("/dislike/{questionId}")
    public ResponseEntity<Map<String, Integer>> dislike(@PathVariable(name = "questionId") Integer questionId) {
        // Call the service to increment warn count
        questionBoardService.incrementWarnCount(questionId);

        // Fetch the updated QuestionBoard to return the latest warnCount
        QuestionBoardDTO questionBoard = questionBoardService.get(questionId);

        Map<String, Integer> response = new HashMap<>();
        response.put("warnCount", questionBoard.getWarnCount());
        return ResponseEntity.ok(response);
    }
}
