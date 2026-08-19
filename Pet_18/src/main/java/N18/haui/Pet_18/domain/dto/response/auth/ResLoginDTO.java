package N18.haui.Pet_18.domain.dto.response.auth;

import N18.haui.Pet_18.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseCookie;


@Setter
@Getter
public class ResLoginDTO {

    private String accessToken;

    private UserLoginDTO user;





    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginDTO{
        private String id;
        private String email;
        private String name;
        private Role role;
    }



}
