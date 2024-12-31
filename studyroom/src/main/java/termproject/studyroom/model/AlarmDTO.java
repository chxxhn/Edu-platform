package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AlarmDTO {

    private Integer alarmId;

    @NotNull
    @Size(max = 100)
    private String content;

    private AlarmType alarmType;


    private Boolean readState = false;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer lectureId;

    private Integer BoardId = null;

}
