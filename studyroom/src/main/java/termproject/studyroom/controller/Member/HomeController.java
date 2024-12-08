package termproject.studyroom.controller.Member;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import termproject.studyroom.config.auto.CustomUserDetails;


@Controller
public class HomeController {

    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    @GetMapping("/")
    public String index(@ModelAttribute("user") CustomUserDetails user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }        return "home/home";
    }

    @GetMapping("/list")
    public String list(@ModelAttribute("user") CustomUserDetails user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }
        return "home/index";
    }

    @GetMapping("/approve")
    public String approve(@ModelAttribute("user") CustomUserDetails user, Model model) {
        model.addAttribute("username", user.getUsername());
        return "home/approve";
    }
}
