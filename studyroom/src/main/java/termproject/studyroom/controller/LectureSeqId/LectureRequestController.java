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
import termproject.studyroom.domain.User;
import termproject.studyroom.model.Grade;
import termproject.studyroom.model.LectureListDTO;
import termproject.studyroom.model.LectureRequestDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureRequestRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.LectureRequestService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/lectureRequests")
public class LectureRequestController {

    private final LectureRequestService lectureRequestService;
    private final LectureListRepository lectureListRepository;
    private final UserRepository userRepository;
    private final LectureRequestRepository lectureRequestRepository;

    public LectureRequestController(final LectureRequestService lectureRequestService,
                                    final LectureListRepository lectureListRepository, UserRepository userRepository, LectureRequestRepository lectureRequestRepository) {
        this.lectureRequestService = lectureRequestService;
        this.lectureListRepository = lectureListRepository;
        this.userRepository = userRepository;
        this.lectureRequestRepository = lectureRequestRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
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

        Page<LectureRequest> paging = this.lectureRequestService.getList(page);
        model.addAttribute("paging", paging);
        model.addAttribute("lectureRequests", lectureRequestService.findAll());
        // 사용자 정보 추가
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");

        return "lectureRequest/list";
    }

    @GetMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId", required = false) Integer lectureId,
            Model model, @AuthenticationPrincipal CustomUserDetails user,
            @ModelAttribute("lectureRequest") final LectureRequestDTO lectureRequestDTO) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("user", user.getUser());
        return "lectureRequest/add";
    }

    @PostMapping("/add/{lectureId}")
    public String add(@PathVariable(name = "lectureId") Integer lectureId,
            @ModelAttribute("lectureRequest") final LectureRequestDTO lectureRequestDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal CustomUserDetails user) {
        // 강의 ID로 LectureList를 조회
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lectureRequestDTO.setAuthor(author);
        lectureRequestService.create(lectureRequestDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureRequest.create.success"));
        return "redirect:/lectureRequests/" + lectureId;
    }

    @GetMapping("/edit/{lectureId}/{rqId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "rqId") final Integer rqId, final Model model,
                       @AuthenticationPrincipal CustomUserDetails user,
                       final RedirectAttributes redirectAttributes) {
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        User currentUser = user.getUser();
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("rqId",rqId);
        model.addAttribute("lectureRequest", lectureRequestService.get(rqId));
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        // TA의 경우 승인 여부 화면으로 이동
        if (currentUser.getGrade() == Grade.PROF) {
            model.addAttribute("isPROF", true);
            return "lectureRequest/approve";
        }

        return "lectureRequest/edit";
    }

    @PostMapping("/approve/{lectureId}/{rqId}")
    public String approve(@PathVariable(name = "lectureId") final Integer lectureId,
                          @PathVariable(name = "rqId") final Integer rqId,
                          @RequestParam(name = "lectureValid", required = false, defaultValue = "false") boolean lectureValid,
                          final RedirectAttributes redirectAttributes,
                          @AuthenticationPrincipal CustomUserDetails user) {
        // 현재 사용자 확인
        User currentUser = user.getUser();
        if (currentUser == null || currentUser.getGrade() != Grade.PROF) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("권한이 없습니다."));
            return "redirect:/lectureRequests/" + lectureId;
        }
        // LectureRequest 데이터 가져오기
        LectureRequestDTO lectureRequest = lectureRequestService.get(rqId);
        if (lectureRequest != null) {
            // lectureValid 값 업데이트
            lectureRequest.setLectureValid(lectureValid);
            lectureRequestService.update(rqId, lectureRequest);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage(
                    lectureValid ? "승인이 완료되었습니다." : "승인이 취소되었습니다."));
        } else {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage("해당 요청을 찾을 수 없습니다."));
        }

        return "redirect:/lectureRequests/" + lectureId;
    }


    @PostMapping("/edit/{lectureId}/{rqId}")
    public String edit(@PathVariable(name = "lectureId") Integer lectureId,
            @PathVariable(name = "rqId") final Integer rqId,
            @ModelAttribute("lectureRequest") final LectureRequestDTO lectureRequestDTO,
            final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user) {
//         세션 유저 정보를 조회
        User author = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // DTO에 강의 및 작성자 설정
        lectureRequestDTO.setAuthor(author);
        lectureRequestService.update(rqId, lectureRequestDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureRequest.update.success"));
        return "redirect:/lectureRequests/" + lectureId;
    }

    @PostMapping("/delete/{lectureId}/{rqId}")
    public String delete(@PathVariable(name = "lectureId") Integer lectureId,
                         @PathVariable(name = "rqId") final Integer rqId,
            final RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails user) {
        lectureRequestService.delete(rqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lectureRequest.delete.success"));
        return "redirect:/lectureRequests/" + lectureId;
    }

    @GetMapping(value = "/detail/{lectureId}/{rqId}")
    public String detail(@PathVariable(name = "lectureId") Integer lectureId, @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable(name = "rqId") final Integer rqId, final Model model) {
        LectureRequestDTO request = lectureRequestService.get(rqId);
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);
        model.addAttribute("lectureRequest", request);
        model.addAttribute("user", user != null ? user.getUser() : "Anonymous User");
        return "lectureRequest/detail";
    }

}
