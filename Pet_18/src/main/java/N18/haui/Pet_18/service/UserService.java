package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqUserUpdateProfile;
import N18.haui.Pet_18.domain.dto.request.UserCreateDto;
import N18.haui.Pet_18.domain.dto.request.UserUpdateDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface UserService {


    UserDto getUserById(String userId);

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(UserUpdateDto userUpdateDto);

    CommonResponseDto deleteUser(String id);

    ResultPaginationDto getAllUser(List<String> filter, Pageable pageable);

    CommonResponseDto changeUserStatus(String id);

    User getUserLogin();

    User getUserByEmail(String email);

    void updateUserToken(String token, String email);

    User getUserWithRoleAndPermissions(String id);

    CommonResponseDto addAvatar(String userId, MultipartFile file) throws URISyntaxException, IOException;

    UserDto updateProfile(ReqUserUpdateProfile reqUserUpdateProfile);


    UserDto getUserProfile();


}
