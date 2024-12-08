package termproject.studyroom.controller.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import termproject.studyroom.config.auto.CustomUserDetails;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.repos.LectureListRepository;

import java.util.Optional;

@Controller
public class LoginHomeController {


    private final LectureListRepository lectureRepository;

    public LoginHomeController(LectureListRepository lectureListRepository) {
        this.lectureRepository = lectureListRepository;
    }

    @ModelAttribute("user")
    public CustomUserDetails getSessionUser(@AuthenticationPrincipal CustomUserDetails user) {
        return user; // Security의 인증 객체에서 세션 정보를 가져옵니다.
    }

    @GetMapping("/mainhome")
    public String mainhome(Model model, @ModelAttribute("user") CustomUserDetails user) {
        if (user != null) {
            model.addAttribute("username", user.getUsername());
        } else {
            model.addAttribute("username", "Anonymous User");
        }
        return "mainhome";
    }

    @PostMapping("/lecture/select")
    public String selectLecture(@RequestParam("lectureId") Integer lectureId, Model model) {
        // 강의 ID로 강의를 찾음
        Optional<LectureList> selectedLecture = lectureRepository.findByLectureId(lectureId);
        if (selectedLecture.isPresent()) {
            LectureList lecture = selectedLecture.get();
            // 강의 이름을 URL에 포함하여 리다이렉트
            return "redirect:/lecture/" + lecture.getName();
        } else {
            model.addAttribute("error", "Lecture not found.");
            return "mainhome"; // 에러 시 홈으로 돌아감
        }
    }

    @GetMapping("/lecture/{lectureName}")
    public String lectureHome(@PathVariable("lectureName") String lectureName,
                              @ModelAttribute("user") CustomUserDetails user,
                              Model model) {
        // 강의 이름으로 강의를 검색
        Optional<LectureList> selectedLecture = lectureRepository.findByName(lectureName);
        if (selectedLecture.isPresent()) {
            model.addAttribute("selectedLecture", selectedLecture.get());
        } else {
            model.addAttribute("error", "Lecture not found.");
        }
        model.addAttribute("username", user != null ? user.getUsername() : "Anonymous User");
        return "lectureHome";
    }

}
