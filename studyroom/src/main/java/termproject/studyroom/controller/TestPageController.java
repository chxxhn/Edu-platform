package termproject.studyroom.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import termproject.studyroom.config.auto.CustomUserDetails;

@Controller
public class TestPageController {

    // 세션에서 User 정보를 가져오는 메서드
    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    // Test 페이지
    @GetMapping("/testpage")
    public String testPage(Model model, @ModelAttribute("user") CustomUserDetails user) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        } else {
            model.addAttribute("username", "Anonymous User");
        }
        return "testpage";
    }
}
