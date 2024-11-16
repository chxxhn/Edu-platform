package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupBoardCommentDTO {

    private Integer gbcomId;

    @NotNull
    private String content;

    @NotNull
    private Integer author;

    @NotNull
    private Integer gbId;

}
