package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.User;


@Getter
@Setter
public class SharingCommentDTO {

    private Integer shcomId;

    @NotNull
    private String content;

    @NotNull
    private User author;

    @NotNull
    private Integer shId;

}
