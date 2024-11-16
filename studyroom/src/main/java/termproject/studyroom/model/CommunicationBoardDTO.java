package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


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
    private Integer author;

    @NotNull
    private Integer lectureId;

}
