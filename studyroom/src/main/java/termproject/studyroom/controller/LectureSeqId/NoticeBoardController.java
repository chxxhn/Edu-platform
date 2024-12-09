package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;
import termproject.studyroom.domain.NoticeBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.NoticeBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.NoticeBoardRepository;
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
    private final NoticeBoardRepository noticeBoardRepository;

    public NoticeBoardController(final NoticeBoardService noticeBoardService,
                                 final UserRepository userRepository,
                                 final LectureListRepository lectureListRepository, NoticeBoardRepository noticeBoardRepository) {
        this.noticeBoardService = noticeBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.noticeBoardRepository = noticeBoardRepository;
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
            final Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);

        Page<NoticeBoard> paging = this.noticeBoardService.getList(page);
        model.addAttribute("paging", paging);
        model.addAttribute("noticeBoards", noticeBoardService.findAll());
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "noticeBoard/list";
    }

    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
            @ModelAttribute("noticeBoard") final NoticeBoardDTO noticeBoardDTO,
                      final Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "noticeBoard/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
            @ModelAttribute("noticeBoard") final NoticeBoardDTO noticeBoardDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      final Model model, @AuthenticationPrincipal CustomUserDetails user) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        noticeBoardDTO.setAuthor(author);
        noticeBoardService.create(noticeBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeBoard.create.success"));
        return "redirect:/noticeBoards/" + lectureId;
    }

    @GetMapping("/edit/{lectureId}/{noticeId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "noticeId") final Integer noticeId, final Model model,
                       @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("noticeBoard", noticeBoardService.get(noticeId));
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "noticeBoard/edit";
    }

    @PostMapping("/edit/{lectureId}/{noticeId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "noticeId") final Integer noticeId,
                       @ModelAttribute("noticeBoard") @Valid final NoticeBoardDTO noticeBoardDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                       final Model model, @AuthenticationPrincipal CustomUserDetails user) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        noticeBoardDTO.setAuthor(author);
        noticeBoardService.update(noticeId, noticeBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("noticeBoard.update.success"));
        return "redirect:/noticeBoards/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{noticeId}")
    public String delete(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "noticeId") final Integer noticeId,
                         final RedirectAttributes redirectAttributes,
                         final Model model, @AuthenticationPrincipal CustomUserDetails user) {
        noticeBoardService.delete(noticeId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("noticeBoard.delete.success"));
        return "redirect:/noticeBoards/" + lectureId;
    }

    @GetMapping(value = "/detail/{lectureId}/{noticeId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "noticeId") final Integer noticeId, final Model model,
                         @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        NoticeBoardDTO notice = noticeBoardService.get(noticeId);
        model.addAttribute("noticeBoard", notice);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "noticeBoard/detail";
    }

}