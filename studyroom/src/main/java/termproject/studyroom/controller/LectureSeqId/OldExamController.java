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
import termproject.studyroom.domain.OldExam;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.OldExamDTO;
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.OldExamRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.OldExamService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.List;


@Controller
@RequestMapping("/oldExams")
public class OldExamController {

    private final OldExamService oldExamService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final OldExamRepository oldExamRepository;

    public OldExamController(final OldExamService oldExamService,
                             final UserRepository userRepository,
                             final LectureListRepository lectureListRepository, OldExamRepository oldExamRepository) {
        this.oldExamService = oldExamService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.oldExamRepository = oldExamRepository;
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
    public String list(@PathVariable(name = "lectureId", required = false) Integer lectureId, final Model model,
                       @AuthenticationPrincipal CustomUserDetails user, @RequestParam(value="page", defaultValue="0") int page) {
        if (lectureId != null) {
            // 특정 강의 질문 페이징 처리
            LectureList lecture = lectureListRepository.findById(lectureId)
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
            model.addAttribute("selectedLecture", lecture);
            Page<OldExam> paging = oldExamRepository.findByLectureIdWithPaging(lecture, PageRequest.of(page, 10));
            List<OldExamDTO> oldExams = oldExamService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("oldExams", oldExams);
        } else {
            // 전체 질문 페이징 처리
            Page<OldExam> paging = oldExamService.getList(page);
            List<OldExamDTO> oldExams = oldExamService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("oldExams", oldExams);
        }
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");

        return "oldExam/list";
    }

    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId, @ModelAttribute("oldExam") final OldExamDTO oldExamDTO,
                      Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "oldExam/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
                      @ModelAttribute("oldExam") final OldExamDTO oldExamDTO,
                        final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                        @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 강의 ID로 LectureList를 조회
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // DTO에 강의 및 작성자 설정
        oldExamDTO.setAuthor(author);
        oldExamService.create(oldExamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExam.create.success"));
        return "redirect:/oldExams/" + lectureId;
    }

    @GetMapping("/edit/{lectureId}/{oeId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "oeId") final Integer oeId, final Model model,
                       @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("oldExam", oldExamService.get(oeId));
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "oldExam/edit";
    }

    @PostMapping("/edit/{lectureId}/{oeId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "oeId") final Integer oeId,
            @ModelAttribute("oldExam") @Valid final OldExamDTO oldExamDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                       @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        oldExamDTO.setAuthor(author);
        oldExamService.update(oeId, oldExamDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("oldExam.update.success"));
        return "redirect:/oldExams/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{oeId}")
    public String delete(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "oeId") final Integer oeId,
            final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user) {
        final ReferencedWarning referencedWarning = oldExamService.getReferencedWarning(oeId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            oldExamService.delete(oeId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("oldExam.delete.success"));
        }
        return "redirect:/oldExams/" + lectureId;
    }

    @GetMapping(value = "/detail/{lectureId}/{oeId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId, @PathVariable(name = "oeId") final Integer oeId, final Model model
    , @AuthenticationPrincipal CustomUserDetails user) {
        OldExamDTO oldExam = oldExamService.get(oeId);

        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("oldExam", oldExam);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "oldExam/detail";
    }

}
