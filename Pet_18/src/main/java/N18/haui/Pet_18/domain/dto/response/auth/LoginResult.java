package N18.haui.Pet_18.domain.dto.response.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
public class LoginResult {
        private ResLoginDTO resLoginDTO;
        private ResponseCookie responseCookie;
}
