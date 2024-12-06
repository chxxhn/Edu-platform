package termproject.studyroom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import termproject.studyroom.domain.User;


@Getter
@Setter
public class QuestionCommentDTO {

    private Integer qcomId;

    @NotNull
    @Size(max = 255)
    private String content;

    @NotNull
    private User author;

    @NotNull
    @JsonProperty("qId")
    private Integer qId;

    private boolean editing;

}
