package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.RoleDto;
import N18.haui.Pet_18.domain.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = PermissionMapper.class
)
public interface RoleMapper {

    RoleDto toDto(Role role);
    List<RoleDto> toDtoList(List<Role> roles);
}
