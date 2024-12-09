package termproject.studyroom.controller.LectureSeqId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import termproject.studyroom.model.BoardType;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.model.WarnDTO;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.LikeService;
import termproject.studyroom.service.WarnService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/warns")
public class WarnController {
    private final WarnService warnService;

    public WarnController(WarnService warnService) {
        this.warnService = warnService;
    }

    // 좋아요 추가 요청
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addLike(@RequestBody WarnDTO warnDTO) {
        try {
            Integer status = warnService.addWarn(warnDTO);
            int warnCount = warnService.getWarnCount(warnDTO.getPostId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("warnCount", warnCount);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unexpected error"));
        }
    }

}