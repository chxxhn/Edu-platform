package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    @NotNull
    private Integer stdId;

    @NotNull
    @Size(max = 10)
    private String name;

    @NotNull
    @Size(max = 10)
    @UserNicknameUnique
    private String nickname;

    @NotNull
    @Size(max = 20)
    private String password;

    @NotNull
    @Size(max = 30)
    @UserEmailUnique
    private String email;

    @NotNull
    private Grade grade;

}
