package termproject.studyroom.controller.LectureSeqId;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import termproject.studyroom.model.BoardType;
import termproject.studyroom.model.LikeDTO;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.LikeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;
    private final UserRepository userRepository;

    public LikeController(LikeService likeService, UserRepository userRepository) {
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    @GetMapping("/add/{postId}/{boardType}")
    public ResponseEntity<List<String>> getLikeUsers(
            @PathVariable Integer postId,
            @PathVariable BoardType boardType) {

        List<String> likerNames = likeService.getLikerNames(postId, boardType);
        return ResponseEntity.ok(likerNames);
    }

    // 좋아요 추가 요청
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addLike(@RequestBody LikeDTO likeDTO) {
        Integer status = likeService.addLike(likeDTO); // 1 = 추가됨, -1 = 취소됨
        int likeCount = likeService.getLikeCount(likeDTO.getPostId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

}