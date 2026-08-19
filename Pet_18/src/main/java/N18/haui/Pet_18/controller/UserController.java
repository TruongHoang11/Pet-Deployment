package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqUserUpdateProfile;
import N18.haui.Pet_18.domain.dto.request.UserCreateDto;
import N18.haui.Pet_18.domain.dto.request.UserUpdateDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping(UrlConstant.User.GET_USER)
    public ResponseEntity<?> getUser(@PathVariable String id){
            return VsResponseUtil.success(HttpStatus.OK,userService.getUserById(id) );

    }

    @PostMapping(UrlConstant.User.CREATE_USER)
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreateDto userCreateDto){
        UserDto userDto = userService.createUser(userCreateDto);
        return VsResponseUtil.success(HttpStatus.OK, userDto);
    }

    @PutMapping(UrlConstant.User.UPDATE_USER)
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdateDto user){
            return VsResponseUtil.success(HttpStatus.OK,userService.updateUser(user) );

    }

    @DeleteMapping(UrlConstant.User.DELETE_USER)
    public ResponseEntity<?> deleteUser(@PathVariable String id){
            CommonResponseDto dto = userService.deleteUser(id);
            return VsResponseUtil.success(HttpStatus.OK, dto);

    }

    @GetMapping(UrlConstant.User.GET_USERS)
    public ResponseEntity<?> getAllUser(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

            return VsResponseUtil.success(HttpStatus.OK,userService.getAllUser(filter,pageable) );

    }

    @PatchMapping(UrlConstant.User.CHANGE_USER_STATUS)
    public ResponseEntity<?> changeUserStatus(@PathVariable String id){
            CommonResponseDto dto = userService.changeUserStatus(id);
            return VsResponseUtil.success(HttpStatus.OK, dto);

    }

    @PostMapping(UrlConstant.User.ADD_AVATAR)
    public ResponseEntity<?> addAvatar(@RequestParam MultipartFile file,
                                       @PathVariable  String userId
                                       ) throws URISyntaxException, IOException {
        CommonResponseDto commonResponseDto =  userService.addAvatar(userId, file);;
            return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);


    }

        @PutMapping(UrlConstant.User.UPDATE_PROFILE)
        public ResponseEntity<?> updateProfile(@RequestBody @Valid ReqUserUpdateProfile reqUserUpdateProfile){
            UserDto userDto = userService.updateProfile(reqUserUpdateProfile);
            return VsResponseUtil.success(HttpStatus.OK, userDto);
        }

        @GetMapping(UrlConstant.User.GET_PROFILE)
        public ResponseEntity<?> getUserProfile(){
            UserDto userDto = userService.getUserProfile();
            return VsResponseUtil.success(HttpStatus.OK, userDto);
        }

}
