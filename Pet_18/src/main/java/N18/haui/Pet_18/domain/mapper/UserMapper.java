package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.request.ReqRegister;
import N18.haui.Pet_18.domain.dto.request.UserCreateDto;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  // Nếu các trường trong UserCreateDto và User giống hệt nhau, không cần @Mapping
  User toUser(UserCreateDto userCreateDTO);

  // Map từ User.role.name sang UserDto.roleName
  @Mapping(target = "roleName", source = "role.name")
  UserDto toUserDto(User user);

  List<UserDto> toUserDtos(List<User> users);

  UserDto toUserDtoRegister(ReqRegister register);
}