package termproject.studyroom.controller.Member;

import jakarta.validation.Valid;
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
import termproject.studyroom.model.Grade;
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.ReferencedWarning;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/users")
public class MUserController {

    private final UserService userService;

    public MUserController(final UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("gradeValues", Grade.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/profile/{lectureId}")
    public String profile(@ModelAttribute("user") final UserDTO userDTO, @PathVariable(name = "lectureId", required = false) Integer lectureId ,
                          @AuthenticationPrincipal CustomUserDetails user) {
        return "user/profile";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("user") final UserDTO userDTO) {
        return "user/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("user") @Valid final UserDTO userDTO,
                      final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/add";
        }
        Integer stdId = userDTO.getStdId();
        if (userService.stdIdExists(stdId)) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user_STdId is exits"));
            return "redirect:/users/add";
        }
        userDTO.setGrade(Grade.STD);
        userService.create(userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.create.success"));
        return "redirect:/login";
    }

    @GetMapping("/edit/{stdId}")
    public String edit(@PathVariable(name = "stdId") final Integer stdId, final Model model) {
        model.addAttribute("user", userService.get(stdId));
        return "user/edit";
    }

    @PostMapping("/edit/{stdId}")
    public String edit(@PathVariable(name = "stdId") final Integer stdId,
                       @ModelAttribute("user") @Valid final UserDTO userDTO, final BindingResult bindingResult,
                       final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            System.out.println("dddd");
            return "user/edit";
        }
        userService.update(stdId, userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/users";
    }

    @PostMapping("/delete/{stdId}")
    public String delete(@PathVariable(name = "stdId") final Integer stdId,
                         final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(stdId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            userService.delete(stdId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("user.delete.success"));
        }
        return "redirect:/users";
    }

}
