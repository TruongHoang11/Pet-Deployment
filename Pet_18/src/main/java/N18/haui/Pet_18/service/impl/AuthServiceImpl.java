package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.constant.RoleConstant;
import N18.haui.Pet_18.domain.dto.request.ReqRegister;
import N18.haui.Pet_18.domain.dto.request.auth.ReqLoginDTO;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.dto.response.auth.LoginResult;
import N18.haui.Pet_18.domain.dto.response.auth.ResLoginDTO;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ConflictException;
import N18.haui.Pet_18.repository.RoleRepository;
import N18.haui.Pet_18.repository.UserRepository;
import N18.haui.Pet_18.security.UserPrincipal;
import N18.haui.Pet_18.security.jwt.JwtTokenProvider;
import N18.haui.Pet_18.service.AuthService;
import N18.haui.Pet_18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Value("${jwt.refresh.expiration_time}")
    private Long refreshExpiration;


    @Override
    public LoginResult login(ReqLoginDTO req, HttpServletRequest request) {
        // cach 1: goi theo UsernamePasswordAuthenticationToken (class cu the)
        //nap input gom username/password vao security
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());

        // cach 2: goi theo Authentication interface
//        Authentication authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword();
     Authentication authentication;
        try {
            // Xác thực người dùng -> Nếu sai tài khoản hoặc sai mật khẩu, hệ thống sẽ ném ngoại lệ ở đây
             authentication = authenticationManager.authenticate(authenticationToken);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadRequestException("Tài khoản hoặc mật khẩu không chính xác");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUser = userService.getUserByEmail(req.getEmail());
        if (currentUser != null) {
            ResLoginDTO.UserLoginDTO userLoginDTO = new ResLoginDTO.UserLoginDTO(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName(),
                    currentUser.getRole()
            );

            resLoginDTO.setUser(userLoginDTO);
        }

        //create access token
        String accessToken = jwtTokenProvider.generateToken(userPrincipal, false);

        resLoginDTO.setAccessToken(accessToken);

        // create refresh token
        String refreshToken = jwtTokenProvider.generateToken(userPrincipal, true);
        userService.updateUserToken(refreshToken, req.getEmail());

        ResponseCookie springCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshExpiration)
                .build();


        LoginResult loginResult = new LoginResult();
        loginResult.setResLoginDTO(resLoginDTO);
        loginResult.setResponseCookie(springCookie);

        return loginResult;
    }



    @Override
    public ResLoginDTO getNewRefreshToken(String refreshToken) {
        return null;
    }



    @Override
    @Transactional
    public UserDto register(ReqRegister reqRegister) {
        if(userRepository.existsByEmailAndDeleteFlagFalse(reqRegister.getEmail())){
            throw new ConflictException("Email already exists");
        }
        if(!reqRegister.getPassword().equals(reqRegister.getConfirmPassword())){
            throw new BadRequestException("Password and confirm password do not match");
        }
        User registerUser = new User();
        registerUser.setName(reqRegister.getName());
        registerUser.setEmail(reqRegister.getEmail());

        // registerUser.setDateOfBirth(reqRegister.getDateOfBirth());
        // registerUser.setGender(GenderEnum.valueOf(reqRegister.getGender()));
        registerUser.setPassword(passwordEncoder.encode(reqRegister.getPassword()));
        registerUser.setRole(roleRepository.findByNameAndDeleteFlagFalse(RoleConstant.USER).orElse(null));
        userRepository.save(registerUser);
        UserDto registerResponseDto = new UserDto();
        registerResponseDto.setId(registerUser.getId());

        registerResponseDto.setDateOfBirth(registerUser.getDateOfBirth());
        registerResponseDto.setEmail(registerUser.getEmail());
        registerResponseDto.setGender(registerUser.getGender());
        registerResponseDto.setName(registerUser.getName());
        registerResponseDto.setLastModifiedDate(registerUser.getLastModifiedDate());
        registerResponseDto.setCreatedDate(registerUser.getCreatedDate());


        return registerResponseDto;
    }
}
