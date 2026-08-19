package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.PermissionDto;
import N18.haui.Pet_18.domain.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDto toDto(Permission permission);

    List<PermissionDto> toDtoList(List<Permission> permissions);

}