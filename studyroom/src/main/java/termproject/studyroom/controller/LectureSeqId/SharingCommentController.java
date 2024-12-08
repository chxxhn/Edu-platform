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
import termproject.studyroom.domain.SharingBoard;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.SharingCommentDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.SharingBoardRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.SharingCommentService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;

import java.util.Optional;


@Controller
@RequestMapping("/sharingComments")
public class SharingCommentController {

    private final SharingCommentService sharingCommentService;
    private final UserRepository userRepository;
    private final SharingBoardRepository sharingBoardRepository;
    private final LectureListRepository lectureListRepository;

    public SharingCommentController(final SharingCommentService sharingCommentService,
                                    final UserRepository userRepository,
                                    final SharingBoardRepository sharingBoardRepository, LectureListRepository lectureListRepository) {
        this.sharingCommentService = sharingCommentService;
        this.userRepository = userRepository;
        this.sharingBoardRepository = sharingBoardRepository;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("shIdValues", sharingBoardRepository.findAll(Sort.by("sharingId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(SharingBoard::getSharingId, SharingBoard::getTitle)));
    }

    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("sharingComments", sharingCommentService.findAll());
        return "sharingComment/list";
    }

    @PostMapping("/add/{sharingId}")
    public String add(
            @PathVariable(name = "sharingId") final Integer sharingId, @RequestParam(value="content") String content,
            @RequestParam(value="lectureId") Integer lectureId, Model model,@AuthenticationPrincipal CustomUserDetails user,
            final RedirectAttributes redirectAttributes) {
        SharingCommentDTO sharingCommentDTO = new SharingCommentDTO();

        User loginuser = user.getUser();

        Optional<LectureList> selectedLecture = lectureListRepository.findByLectureId(lectureId);
        if (selectedLecture.isPresent()) {
            model.addAttribute("selectedLecture", selectedLecture.get());
        } else {
            model.addAttribute("error", "Lecture not found.");
        }

        sharingCommentDTO.setContent(content);
        sharingCommentDTO.setAuthor(loginuser);
        sharingCommentDTO.setShId(sharingId);
        sharingCommentService.create(sharingCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingComment.create.success"));
        return "redirect:/sharingBoards/detail/" + lectureId+ "/" + sharingId;
    }

    @GetMapping("/edit/{shcomId}")
    public String edit(@PathVariable(name = "shcomId") final Integer shcomId, final Model model) {
        model.addAttribute("sharingComment", sharingCommentService.get(shcomId));
        return "sharingComment/edit";
    }

    @PostMapping("/edit/{shcomId}")
    public String edit(@PathVariable(name = "shcomId") final Integer shcomId,
            @ModelAttribute("sharingComment") @Valid final SharingCommentDTO sharingCommentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "sharingComment/edit";
        }
        SharingCommentDTO comment = sharingCommentService.get(shcomId);
        Integer sharingId = comment.getShId();
        sharingCommentService.update(shcomId, sharingCommentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("sharingComment.update.success"));
        return "redirect:/sharingBoards/detail/" + sharingId;
    }

    @PostMapping("/delete/{shcomId}")
    public String delete(@PathVariable(name = "shcomId") final Integer shcomId,
            final RedirectAttributes redirectAttributes) {
        SharingCommentDTO comment = sharingCommentService.get(shcomId);
        Integer sharingId = comment.getShId();
        sharingCommentService.delete(shcomId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("sharingComment.delete.success"));
        return "redirect:/sharingBoards/detail/" + sharingId;
    }

}
