package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SharingCommentDTO {

    private Integer shcomId;

    @NotNull
    private String content;

    @NotNull
    private Integer author;

    @NotNull
    private Integer shId;

}
