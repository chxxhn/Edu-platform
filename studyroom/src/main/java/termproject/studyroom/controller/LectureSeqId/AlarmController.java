package termproject.studyroom.controller.LectureSeqId;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.AlarmDTO;
import termproject.studyroom.model.AlarmType;
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.LectureUserRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.AlarmService;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LectureListRepository lectureListRepository;

    public AlarmController(final AlarmService alarmService, final UserRepository userRepository,
                           final UserService userService,
                           final LectureListRepository lectureListRepository) {
        this.alarmService = alarmService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("alarmTypeValues", AlarmType.values());
        model.addAttribute("userIdValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("alarms", alarmService.findAll());
        return "alarm/list";
    }


    @GetMapping("/{lectureId}")
    public String alarmList(@ModelAttribute("user") final UserDTO userDTO, @PathVariable(name = "lectureId", required = false) Integer lectureId ,
                       @AuthenticationPrincipal CustomUserDetails user, final Model model) {
        User loginuser = user.getUser();
        model.addAttribute("alarms", alarmService.findAlarm(loginuser));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", loginuser);

        // 특정 강의 질문 페이징 처리
        LectureList lecture = lectureListRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));
        model.addAttribute("selectedLecture", lecture);

        return "alarm/detail";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("alarm") final AlarmDTO alarmDTO) {
        return "alarm/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("alarm") @Valid final AlarmDTO alarmDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "alarm/add";
        }
        alarmService.create(alarmDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("alarm.create.success"));
        return "redirect:/alarms";
    }

    @GetMapping("/edit/{alarmId}")
    public String edit(@PathVariable(name = "alarmId") final Integer alarmId, final Model model) {
        model.addAttribute("alarm", alarmService.get(alarmId));
        return "alarm/edit";
    }

    @PostMapping("/edit/{alarmId}")
    public String edit(@PathVariable(name = "alarmId") final Integer alarmId,
            @ModelAttribute("alarm") @Valid final AlarmDTO alarmDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "alarm/edit";
        }
        alarmService.update(alarmId, alarmDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("alarm.update.success"));
        return "redirect:/alarms";
    }

    @PostMapping("/delete/{alarmId}")
    public String delete(@PathVariable(name = "alarmId") final Integer alarmId,
            final RedirectAttributes redirectAttributes) {
        alarmService.delete(alarmId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("alarm.delete.success"));
        return "redirect:/alarms";
    }

}
