package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.CategoryDto;
import N18.haui.Pet_18.domain.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categories);
}
