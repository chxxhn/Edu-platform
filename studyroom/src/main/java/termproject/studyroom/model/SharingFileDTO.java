package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SharingFileDTO {

    private Integer shfId;

    @NotNull
    @Size(max = 255)
    private String src;

    @NotNull
    private UUID uuid;

    @NotNull
    @Size(max = 50)
    private String fileName;

    @NotNull
    @SharingFileShIdUnique
    private Integer shId;

}
