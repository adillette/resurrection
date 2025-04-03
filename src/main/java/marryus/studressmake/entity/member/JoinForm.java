package marryus.studressmake.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter

public class JoinForm {
    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String membername;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    private String email;

    public JoinForm() {
        super();
    }


}
