package termproject.studyroom.controller.LectureSeqId;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import termproject.studyroom.domain.GroupApprove;
import termproject.studyroom.domain.GroupBoard;
import termproject.studyroom.repos.GroupApproveRepository;
import termproject.studyroom.repos.GroupBoardRepository;

import java.util.List;

@Controller
@RequestMapping("/groupApproves")
public class GroupApproveController {

    private final GroupApproveRepository groupApproveRepository;
    private final GroupBoardRepository groupBoardRepository;

    public GroupApproveController(GroupApproveRepository groupApproveRepository,
                                  GroupBoardRepository groupBoardRepository) {
        this.groupApproveRepository = groupApproveRepository;
        this.groupBoardRepository = groupBoardRepository;
    }

    @GetMapping("/detail/{lectureId}/{gpId}")
    public String showGroupBlog(
            @PathVariable("lectureId") Integer lectureId,
            @PathVariable("gpId") Integer gpId,
            final Model model) {

        // GroupApprove 데이터 조회
        GroupApprove groupApprove = groupApproveRepository
                .findByLectureList_LectureIdAndGroupProject_GpId(lectureId, gpId)
                .orElseThrow(() -> new IllegalArgumentException("GroupApprove not found for lectureId: " + lectureId + ", gpId: " + gpId));

        // GroupBoard 데이터 조회
        List<GroupBoard> groupBoards = groupBoardRepository.findByGpId_GpId(gpId);

        // 모델에 필요한 데이터 추가
        model.addAttribute("groupApprove", groupApprove);
        model.addAttribute("groupBoards", groupBoards);
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("gpId", gpId);

        return "groupApprove/detail";
    }
}
