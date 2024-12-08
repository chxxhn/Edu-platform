package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.model.QuestionCommentDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.QuestionBoardService;
import termproject.studyroom.service.QuestionCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;

import java.util.List;

@Controller
@RequestMapping("/questionBoards")
public class QuestionBoardController {

    private final QuestionBoardService questionBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final QuestionBoardRepository questionBoardRepository;
    private final QuestionCommentService questionCommentService;

    public QuestionBoardController(final QuestionBoardService questionBoardService,
                                   final UserRepository userRepository,
                                   final LectureListRepository lectureListRepository,
                                   final QuestionBoardRepository questionBoardRepository,
                                   final QuestionCommentService questionCommentService) {
        this.questionBoardService = questionBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.questionBoardRepository = questionBoardRepository;
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


    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    @GetMapping("/{lectureId}")
    public String list(@PathVariable(name = "lectureId", required = false) Integer lectureId,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       final Model model,
                       @AuthenticationPrincipal CustomUserDetails user) {

        if (lectureId != null) {
            // 특정 강의 질문 페이징 처리
            LectureList lecture = lectureListRepository.findById(lectureId)
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
            model.addAttribute("selectedLecture", lecture);
            Page<QuestionBoard> paging = questionBoardRepository.findByLectureIdWithPaging(lecture, PageRequest.of(page, 10));
            List<QuestionBoardDTO> questionBoards = questionBoardService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("questionBoards", questionBoards);
        } else {
            // 전체 질문 페이징 처리
            Page<QuestionBoard> paging = questionBoardService.getList(page);
            List<QuestionBoardDTO> questionBoards = questionBoardService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("questionBoards", questionBoards);
        }

        // 사용자 정보 추가
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");

        return "questionBoard/list";
    }



    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId, @ModelAttribute("questionBoard") final QuestionBoardDTO questionBoardDTO
    ,Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "questionBoard/add";
    }


    @PostMapping("/add/{lectureId}")
    public String addQuestion(@PathVariable(name = "lectureId") Integer lectureId,
                              @ModelAttribute("questionBoard") final QuestionBoardDTO questionBoardDTO,
                              final BindingResult bindingResult,
                              @AuthenticationPrincipal CustomUserDetails user,
                              final RedirectAttributes redirectAttributes,
                              final Model model) {
        // 강의 ID로 LectureList를 조회
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        questionBoardDTO.setAuthor(author);
        // 유효성 검사 실패 시 다시 add 페이지로
        if (bindingResult.hasErrors()) {
            model.addAttribute("selectedLecture", lecture);
            model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                    .stream()
                    .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
            model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                    .stream()
                    .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
            return "questionBoard/add";
        }

        // 질문 등록
        questionBoardService.create(questionBoardDTO);

        // 성공 메시지 추가
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "질문이 성공적으로 추가되었습니다.");
        return "redirect:/questionBoards/" + lectureId;
    }


    @GetMapping("/detail/{lectureId}/{questionId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId,@PathVariable(name = "questionId") Integer questionId,
                         Model model,
                         @AuthenticationPrincipal CustomUserDetails user) {
        QuestionBoardDTO question = questionBoardService.get(questionId);


        // 댓글 조회
        List<QuestionCommentDTO> comments = questionCommentService.findByQuestionId(questionId);

        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        // Model에 데이터 추가
        model.addAttribute("questionBoard", question);
        model.addAttribute("questionComment", comments);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");

        return "questionBoard/detail";
    }


}
