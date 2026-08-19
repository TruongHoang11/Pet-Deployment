package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.CategoryType;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateCategory {
    @NotBlank(message = "Category name is required")
    private String name;

    @EnumValue(name = "categoryType", enumClass = CategoryType.class)
    private String categoryType;
}