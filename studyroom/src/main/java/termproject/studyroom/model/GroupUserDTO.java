package termproject.studyroom.model;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupUserDTO {

    @NotNull
    private Integer userId;

    @NotNull
    private Integer groupId;

    @NotNull
    private Team team;

    @NotNull
    private Integer lectureId;
}