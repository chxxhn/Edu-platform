package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;

import java.util.Optional;


@Controller
@RequestMapping("/questionComments")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    private final UserRepository userRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final LectureListRepository lectureListRepository;

    public QuestionCommentController(final QuestionCommentService questionCommentService,
            final UserRepository userRepository,
            final QuestionBoardRepository questionBoardRepository,
                                     final LectureListRepository lectureListRepository) {
        this.questionCommentService = questionCommentService;
        this.userRepository = userRepository;
        this.questionBoardRepository = questionBoardRepository;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
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
                      @RequestParam(value="lectureId") Integer lectureId,
                      Model model, @AuthenticationPrincipal CustomUserDetails user,
                      final RedirectAttributes redirectAttributes) {
        QuestionCommentDTO questionCommentDTO = new QuestionCommentDTO();

        // 테스트용 user 설정, 나중에 로그인 완성하면 고쳐야함
        User loginuser = user.getUser();
        // 강의 이름으로 강의를 검색
        Optional<LectureList> selectedLecture = lectureListRepository.findByLectureId(lectureId);
        if (selectedLecture.isPresent()) {
            model.addAttribute("selectedLecture", selectedLecture.get());
        } else {
            model.addAttribute("error", "Lecture not found.");
        }

        questionCommentDTO.setContent(content);
        questionCommentDTO.setAuthor(loginuser);
        questionCommentDTO.setQId(questionId);
        questionCommentService.create(questionCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionComment.create.success"));
        return "redirect:/questionBoards/detail/" + lectureId+ "/" + questionId;
    }

    @GetMapping("/edit/{qcomId}")
    public String edit(@PathVariable(name = "qcomId") final Integer qcomId, final Model model) {
        QuestionCommentDTO comment = questionCommentService.get(qcomId);
        model.addAttribute("questionComment", comment);
        return "questionComment/edit";
    }

    @PostMapping("/edit/{qcomId}")
    public String edit(@PathVariable(name = "qcomId") final Integer qcomId,
            @ModelAttribute("questionComment") @Valid final QuestionCommentDTO questionCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "questionComment/edit";
        }
        QuestionCommentDTO comment = questionCommentService.get(qcomId);
        Integer questionId = comment.getQId();
        questionCommentService.update(qcomId, questionCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("questionComment.update.success"));
        return "redirect:/questionBoards/detail/" + questionId;
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
