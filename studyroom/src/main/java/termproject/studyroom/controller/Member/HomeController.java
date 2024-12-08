package termproject.studyroom.controller.Member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "home/home";
    }

    @GetMapping("/list")
    public String list() {
        return "home/index";
    }

    @GetMapping("/approve")
    public String approve() {
        return "home/approve";
    }
}
