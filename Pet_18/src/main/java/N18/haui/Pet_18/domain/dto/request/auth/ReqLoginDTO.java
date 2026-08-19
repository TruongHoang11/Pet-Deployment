package N18.haui.Pet_18.domain.dto.request.auth;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqLoginDTO {

    @NotBlank(message = "email khong duoc de trong")
    private String email;

    @NotBlank(message = "password khong duoc de trong")
    private String password;

}
