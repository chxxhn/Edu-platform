package termproject.studyroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {

    @NotNull(message = "이메일은 필수 입력 사항입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 사항입니다.")
    private String password;

}
