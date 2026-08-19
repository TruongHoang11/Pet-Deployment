package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.request.ReqRegister;
import N18.haui.Pet_18.domain.dto.request.auth.ReqLoginDTO;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.dto.response.auth.LoginResult;
import N18.haui.Pet_18.domain.dto.response.auth.ResLoginDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResult login(ReqLoginDTO req, HttpServletRequest request);


    ResLoginDTO getNewRefreshToken(String refreshToken);



    UserDto register(ReqRegister reqRegister);

}
