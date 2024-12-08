package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupProjectDTO {

    private Integer gpId;

    private Boolean groupValid = false; // 기본값 설정

    @NotNull
    @Size(max = 10)
    @GroupProjectGroupNameUnique
    private String groupName;

    private String groupDetail;

    @NotNull
    private Integer createdUserId;

    @NotNull
    private Integer lectureId;



}
