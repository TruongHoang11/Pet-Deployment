package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.CategoryType;
import N18.haui.Pet_18.domain.dto.request.ReqCreateCategory;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCategory;
import N18.haui.Pet_18.domain.dto.response.CategoryDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.entity.Category;
import N18.haui.Pet_18.domain.mapper.CategoryMapper;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.CategoryRepository;
import N18.haui.Pet_18.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // 1. Tạo category
    @Override
    @Transactional
    public CategoryDto createCategory(ReqCreateCategory req) {
        log.info("[CATEGORY] Tạo category mới: {}", req.getName());

        // Kiểm tra tên đã tồn tại chưa
        if (categoryRepository.existsByNameAndDeleteFlagFalse(req.getName())) {
            throw new BadRequestException("Category with name already exists: " + req.getName());
        }

        Category category = new Category();
        category.setName(req.getName());
        category.setCategoryType(CategoryType.valueOf(req.getCategoryType().toUpperCase().trim()));

        categoryRepository.save(category);
        log.info("[CATEGORY] Tạo thành công | ID: {}", category.getId());
        return categoryMapper.toDto(category);
    }

    // 2. Cập nhật category
    @Override
    @Transactional
    public CategoryDto updateCategory(ReqUpdateCategory req) {
        log.info("[CATEGORY] Cập nhật category ID: {}", req.getId());

        Category category = getCategoryById(req.getId());

        // Kiểm tra tên mới có trùng không
        if (!category.getName().equals(req.getName())
                && categoryRepository.existsByNameAndDeleteFlagFalse(req.getName())) {
            throw new BadRequestException("Category with name already exists: " + req.getName());
        }

        category.setName(req.getName());
        category.setCategoryType(CategoryType.valueOf(req.getCategoryType().toUpperCase().trim()));

        categoryRepository.save(category);
        log.info("[CATEGORY] Cập nhật thành công | ID: {}", req.getId());
        return categoryMapper.toDto(category);
    }

    // 3. Xóa category
    @Override
    @Transactional
    public CommonResponseDto deleteCategory(Long id) {
        log.info("[CATEGORY] Xóa category ID: {}", id);

        Category category = getCategoryById(id);

        // Kiểm tra còn Product đang dùng không
        if (category.getProducts() != null
                && category.getProducts().stream()
                .anyMatch(p -> Boolean.FALSE.equals(p.getDeleteFlag()))) {
            throw new BadRequestException("Can not delete Category when Product is using this Category");
        }

        // Kiểm tra còn Service đang dùng không
        if (category.getPetServices() != null
                && category.getPetServices().stream()
                .anyMatch(s -> Boolean.FALSE.equals(s.getDeleteFlag()))) {
            throw new BadRequestException("Can not delete Category when PetService is using this Category");
        }

        category.setDeleteFlag(Boolean.TRUE);
        categoryRepository.save(category);
        log.info("[CATEGORY] Xóa thành công | ID: {}", id);
        return new CommonResponseDto(true,"Delete Category success");

    }

    // 4. Xem danh sách category
    @Override
    public List<CategoryDto> getCategories() {
        log.info("[CATEGORY] Lấy danh sách category");

        List<Category> categories = categoryRepository
                .findByDeleteFlagFalse();

        log.info("[CATEGORY] Tổng category: {}", categories.size());
        return categoryMapper.toDtoList(categories);
    }

    // 5. Xem chi tiết category
    @Override
    public CategoryDto getCategoryDetail(Long id) {
        log.info("[CATEGORY] Xem chi tiết category ID: {}", id);

        Category category = getCategoryById(id);

        log.info("[CATEGORY] Xem chi tiết thành công | ID: {}", id);
        return categoryMapper.toDto(category);
    }

    // Helper
    public Category getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy category ID: {}", id);
                    return new NotFoundException("Category not found with ID: " + id);
                });

        if (Boolean.TRUE.equals(category.getDeleteFlag())) {
            throw new NotFoundException("Category deleted with ID: " + id);
        }

        return category;
    }
}