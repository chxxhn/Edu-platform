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
    @Size(max = 30)
    private String content;

    private AlarmType alarmType;

    @NotNull
    private Boolean readState;

    @NotNull
    private Integer userId;

}
