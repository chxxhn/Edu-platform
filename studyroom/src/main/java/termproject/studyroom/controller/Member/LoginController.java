package termproject.studyroom.controller.Member;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.model.LoginDTO;
import termproject.studyroom.model.UserDTO;
import termproject.studyroom.service.UserService;
import termproject.studyroom.util.WebUtils;
import jakarta.servlet.http.HttpSession;


@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login"; // login.html 페이지로 이동
    }
//
//    // 로그인 컨트롤러
//    @PostMapping("/login")
//    public String processLogin(
//            @ModelAttribute("loginuser") LoginDTO loginDTO,
//            RedirectAttributes redirectAttributes,
//            HttpSession session,
//            Model model) {
//
//        // 학번과 비밀번호 검증
//        boolean loginSuccess = userService.validateUser(loginDTO.getStdId(), loginDTO.getPassword());
//        if (loginSuccess) {
//            // 세션에 사용자 정보 저장
//            session.setAttribute("userId", loginDTO.getStdId());
//            session.setAttribute("userGrade", "STD"); // 기본 Grade 설정
//            redirectAttributes.addFlashAttribute("loginSuccess", "로그인 성공!");
//            return "redirect:/home"; // 홈 화면으로 리다이렉트
//        } else {
//            model.addAttribute("loginError", "학번 또는 비밀번호가 잘못되었습니다.");
//            return "user/login";
//        }
//    }


}
