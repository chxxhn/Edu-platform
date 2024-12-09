package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


@Getter
@Setter
public class QuestionBoardDTO {

    private Integer questionId;

    @NotNull
    @Size(max = 30)
    private String title;

    @NotNull
    private String content;

    private Integer warnCount;

    private Integer likeCount;

    @NotNull
    private User author;

    @NotNull
    private LectureList lectureId;

}
