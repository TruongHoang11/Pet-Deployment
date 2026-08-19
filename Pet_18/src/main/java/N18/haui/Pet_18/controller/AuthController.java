package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.domain.dto.request.ReqRegister;
import N18.haui.Pet_18.domain.dto.request.auth.ReqLoginDTO;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.dto.response.auth.LoginResult;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.service.AuthService;
import N18.haui.Pet_18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("/api/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody ReqLoginDTO loginRequestDto, HttpServletRequest request){
        LoginResult loginResult = authService.login(loginRequestDto, request);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, loginResult.getResponseCookie().toString());


        return VsResponseUtil.success(headers, HttpStatus.OK, loginResult.getResLoginDTO());
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody ReqRegister register){
        UserDto responseDto = authService.register(register);
        return VsResponseUtil.success(HttpStatus.OK, responseDto);

    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(){
        User currentUser = userService.getUserLogin();
        if (currentUser != null && currentUser.getEmail() != null) {
            this.userService.updateUserToken(null, currentUser.getEmail());
        }
        ResponseCookie deleteSrpingCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE,deleteSrpingCookie.toString()) ;
        return VsResponseUtil.success(headers, HttpStatus.OK, new CommonResponseDto(true, "Logout successfully!"));
    }

}