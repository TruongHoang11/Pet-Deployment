package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.request.ReqCreateCategory;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCategory;
import N18.haui.Pet_18.domain.dto.response.CategoryDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.entity.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(ReqCreateCategory req);

    CategoryDto updateCategory(ReqUpdateCategory req);

    CommonResponseDto deleteCategory(Long id);

    List<CategoryDto> getCategories();

    CategoryDto getCategoryDetail(Long id);

    Category getCategoryById(Long id);
}
