package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.constant.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
    private CategoryType categoryType;
    private Boolean activeFlag;
    private Boolean deleteFlag;
}