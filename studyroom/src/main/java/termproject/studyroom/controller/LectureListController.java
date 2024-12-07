package termproject.studyroom.controller;

import java.security.Principal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.config.auto.LoginUser;
import termproject.studyroom.config.auto.SessionUser;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureUser;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.LectureListDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureUserRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.LectureListService;
import termproject.studyroom.service.LectureUserService;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/lectureLists")
public class LectureListController {

    private final LectureListService lectureListService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LectureListRepository lectureListRepository;
    private final LectureUserRepository lectureUserRepository;
    private final LectureUserService lectureUserService;



    // 세션에서 User 정보를 가져오는 메서드
    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }


    public LectureListController(final LectureListService lectureListService,
                                 final UserRepository userRepository, final UserService userService,LectureListRepository lectureListRepository, LectureUserRepository lectureUserRepository,
                                 LectureUserService lectureUserService) {
        this.lectureListService = lectureListService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.userService = userService;
        this.lectureUserRepository = lectureUserRepository;
        this.lectureUserService = lectureUserService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("stdIdValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
    }


    @GetMapping
    public String list(Model model, @ModelAttribute("user") CustomUserDetails user) {
        User loginUser = user.getUser();
        List<LectureList> myLectures = lectureUserRepository.findLectureListsByUserId(loginUser.getStdId());
        model.addAttribute("lectureLists", lectureListService.findAll());
        model.addAttribute("myLectures", myLectures);
        return "mainhome";
    }

    @PostMapping("/submitLectureCode")
    public String submitLectureCode(
            @RequestParam("lectureCode") Integer lectureCode,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("user") CustomUserDetails user,
            Model model) {

        User loginUser = user.getUser(); // 세션 유저 가져오기

        // 과목 코드 확인
        Optional<LectureList> optionalLecture = lectureListRepository.findById(lectureCode);
        if (optionalLecture.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "해당 과목 코드가 존재하지 않습니다.");
            return "redirect:/mainhome";
        }

        LectureList lectureList = optionalLecture.get();

        // 사용자와 강의 연결
        lectureUserService.enrollUserToLecture(loginUser, lectureList);

        // 등록된 강의 목록 가져오기
        List<LectureList> myLectures = lectureUserRepository.findLectureListsByUserId(loginUser.getStdId());
        model.addAttribute("myLectures", myLectures);

        // 성공 메시지 전달
        redirectAttributes.addFlashAttribute("success", "과목 코드가 성공적으로 등록되었습니다.");
        return "mainhome";
    }



    @GetMapping("/add")
    public String add(@ModelAttribute("lectureList") final LectureListDTO lectureListDTO) {
        return "lectureList/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("lectureList") @Valid final LectureListDTO lectureListDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureList/add";
        }
        lectureListService.create(lectureListDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureList.create.success"));
        return "redirect:/lectureLists";
    }

    @GetMapping("/edit/{lectureId}")
    public String edit(@PathVariable(name = "lectureId") final Integer lectureId,
            final Model model) {
        model.addAttribute("lectureList", lectureListService.get(lectureId));
        return "lectureList/edit";
    }

    @PostMapping("/edit/{lectureId}")
    public String edit(@PathVariable(name = "lectureId") final Integer lectureId,
            @ModelAttribute("lectureList") @Valid final LectureListDTO lectureListDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureList/edit";
        }
        lectureListService.update(lectureId, lectureListDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureList.update.success"));
        return "redirect:/lectureLists";
    }

    @PostMapping("/delete/{lectureId}")
    public String delete(@PathVariable(name = "lectureId") final Integer lectureId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = lectureListService.getReferencedWarning(lectureId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            lectureListService.delete(lectureId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lectureList.delete.success"));
        }
        return "redirect:/lectureLists";
    }
}
