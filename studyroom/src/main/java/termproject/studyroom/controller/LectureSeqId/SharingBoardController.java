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
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.model.SharingBoardDTO;
import termproject.studyroom.model.SharingCommentDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.SharingBoardService;
import termproject.studyroom.service.SharingCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.List;


@Controller
@RequestMapping("/sharingBoards")
public class SharingBoardController {

    private final SharingBoardService sharingBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final SharingCommentService sharingCommentService;

    public SharingBoardController(final SharingBoardService sharingBoardService,
                                  final UserRepository userRepository,
                                  final LectureListRepository lectureListRepository, SharingBoardRepository sharingBoardRepository, SharingCommentService sharingCommentService) {
        this.sharingBoardService = sharingBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.sharingCommentService = sharingCommentService;
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
            Page<SharingBoard> paging = sharingBoardRepository.findByLectureIdWithPaging(lecture, PageRequest.of(page, 10));
            List<SharingBoardDTO> sharingBoards = sharingBoardService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("sharingBoards", sharingBoards);
        } else {
            // 전체 질문 페이징 처리
            Page<SharingBoard> paging = sharingBoardService.getList(page);
            List<SharingBoardDTO> sharingBoards = sharingBoardService.findAll();
            model.addAttribute("paging", paging);
            model.addAttribute("sharingBoards", sharingBoards);
        }

        // 사용자 정보 추가
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");

        return "sharingBoard/list";
    }

    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId, Model model,
                      @ModelAttribute("sharingBoard") final SharingBoardDTO sharingBoardDTO, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "sharingBoard/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId") Integer lectureId, @ModelAttribute("sharingBoard")  final SharingBoardDTO sharingBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 강의 ID로 LectureList를 조회
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // DTO에 강의 및 작성자 설정
        sharingBoardDTO.setUserId(author);
        sharingBoardService.create(sharingBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingBoard.create.success"));
        return "redirect:/sharingBoards/" + lectureId;
    }

    @GetMapping("/edit/{lectureId}/{sharingId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "sharingId") final Integer sharingId,
            final Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("sharingBoard", sharingBoardService.get(sharingId));
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "sharingBoard/edit";
    }

    @PostMapping("/edit/{lectureId}/{sharingId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "sharingId") final Integer sharingId,
            @ModelAttribute("sharingBoard") final SharingBoardDTO sharingBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                       @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        sharingBoardDTO.setUserId(author);
        sharingBoardService.update(sharingId, sharingBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingBoard.update.success"));
        return "redirect:/sharingBoards/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{sharingId}")
    public String delete(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "sharingId") final Integer sharingId,
            final RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal CustomUserDetails user) {
        final ReferencedWarning referencedWarning = sharingBoardService.getReferencedWarning(sharingId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            sharingBoardService.delete(sharingId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("sharingBoard.delete.success"));
        }
        return "redirect:/sharingBoards/" + lectureId;
    }

    @GetMapping(value = "/detail/{lectureId}/{sharingId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId, @PathVariable(name = "sharingId") final Integer sharingId, final Model model
    , @AuthenticationPrincipal CustomUserDetails user) {
        SharingBoardDTO sharing = sharingBoardService.get(sharingId);
        List<SharingCommentDTO> comments = sharingCommentService.findBySharingId(sharingId);
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("sharingBoard", sharing);
        model.addAttribute("sharingComment", comments);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "sharingBoard/detail";
    }

}
