package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


@Getter
@Setter
public class CommunicationBoardDTO {

    private Integer comnId;

    @NotNull
    private String content;

    @NotNull
    private Integer maxnum;

    private Boolean valid;

    @NotNull
    private User author;

    @NotNull
    private LectureList lectureId;

}
