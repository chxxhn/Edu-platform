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
import termproject.studyroom.domain.CommunicationBoard;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.CommunicationBoardDTO;
import termproject.studyroom.repos.CommunicationBoardRepository;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.CommunicationBoardService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/communicationBoards")
public class CommunicationBoardController {

    private final CommunicationBoardService communicationBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final CommunicationBoardRepository communicationBoardRepository;

    public CommunicationBoardController(final CommunicationBoardService communicationBoardService,
                                        final UserRepository userRepository,
                                        final LectureListRepository lectureListRepository, CommunicationBoardRepository communicationBoardRepository) {
        this.communicationBoardService = communicationBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.communicationBoardRepository = communicationBoardRepository;
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
        Page<CommunicationBoard> paging = this.communicationBoardService.getList(page);
        model.addAttribute("paging", paging);
        model.addAttribute("communicationBoards", communicationBoardService.findAll());
        // 사용자 정보 추가
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "communicationBoard/list";
    }

    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
            @ModelAttribute("communicationBoard") final CommunicationBoardDTO communicationBoardDTO,
                      Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "communicationBoard/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
            @ModelAttribute("communicationBoard") final CommunicationBoardDTO communicationBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // DTO에 강의 및 작성자 설정
        communicationBoardDTO.setAuthor(author);
        communicationBoardService.create(communicationBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("communicationBoard.create.success"));
        return "redirect:/communicationBoards/" + lectureId;
    }

    @GetMapping("/edit/{lectureId}/{comnId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "comnId") final Integer comnId, final Model model,
                       @AuthenticationPrincipal CustomUserDetails user) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("communicationBoard", communicationBoardService.get(comnId));
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "communicationBoard/edit";
    }

    @PostMapping("/edit/{lectureId}/{comnId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "comnId") final Integer comnId,
            @ModelAttribute("communicationBoard") @Valid final CommunicationBoardDTO communicationBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                       @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        communicationBoardDTO.setAuthor(author);
        communicationBoardService.update(comnId, communicationBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("communicationBoard.update.success"));
        return "redirect:/communicationBoards/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{comnId}")
    public String delete(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "comnId") final Integer comnId,
            final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user) {
        communicationBoardService.delete(comnId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("communicationBoard.delete.success"));
        return "redirect:/communicationBoards/" + lectureId;
    }

    @GetMapping(value = "/detail/{lectureId}/{comnId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId, @PathVariable(name = "comnId") final Integer comnId, final Model model
    , @AuthenticationPrincipal CustomUserDetails user) {
        CommunicationBoardDTO communication = communicationBoardService.get(comnId);
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("communicationBoard", communication);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "communicationBoard/detail";
    }

}
