package N18.haui.Pet_18.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;

import N18.haui.Pet_18.domain.dto.response.MenuDto;
import N18.haui.Pet_18.domain.entity.Menu;
import N18.haui.Pet_18.domain.entity.Role;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuDto toDto(Menu menu);

    List<MenuDto> toDtos(List<Menu> menus);

    default String map(Role role) {
        return role == null ? null : role.getName();
    }
}