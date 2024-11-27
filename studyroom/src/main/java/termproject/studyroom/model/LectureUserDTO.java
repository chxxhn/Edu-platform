package termproject.studyroom.model;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureUserDTO {

    @NotNull
    private Integer user_id;

    @NotNull
    private Integer lecture_id;
}
