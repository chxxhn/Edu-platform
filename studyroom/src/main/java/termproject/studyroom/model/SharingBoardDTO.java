package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SharingBoardDTO {

    private Integer sharingId;

    @NotNull
    @Size(max = 30)
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Integer warnCount;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer lectureId;

}
