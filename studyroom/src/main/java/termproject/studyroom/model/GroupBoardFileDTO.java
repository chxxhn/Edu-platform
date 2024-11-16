package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupBoardFileDTO {

    private Integer gbfId;

    @NotNull
    @Size(max = 255)
    private String src;

    @NotNull
    private UUID uuid;

    @NotNull
    @Size(max = 255)
    private String fileName;

    @NotNull
    @GroupBoardFileGbIdUnique
    private Integer gbId;

}
