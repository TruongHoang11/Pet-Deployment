package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.CategoryType;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCategory {
    @NotNull(message = "Category id is required")
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    @EnumValue(name = "categoryType", enumClass = CategoryType.class)
    @NotBlank(message = "Category type is required")
    private String categoryType;
}