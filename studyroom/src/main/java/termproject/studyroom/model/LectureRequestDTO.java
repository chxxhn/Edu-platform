package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;


@Getter
@Setter
public class LectureRequestDTO {

    private Integer rqId;

    @NotNull
    @Size(max = 30)
    private String title;

    @NotNull
    private String content;

//    @NotNull
//    private Integer numberDesired;

    @NotNull
    private Boolean lectureValid;

    @NotNull
    private LectureList lectureId;

}
