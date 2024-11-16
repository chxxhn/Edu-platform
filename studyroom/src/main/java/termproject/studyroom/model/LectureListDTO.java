package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LectureListDTO {

    private Integer lectureId;

    @NotNull
    @Size(max = 20)
    @LectureListNameUnique
    private String name;

    @NotNull
    private Integer stdId;

}
