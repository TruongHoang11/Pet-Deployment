package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateCategory;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCategory;
import N18.haui.Pet_18.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestApiV1
public class CategoryController {

    private final CategoryService categoryService;

    // Public
    @GetMapping(UrlConstant.Category.GET_CATEGORIES)
    public ResponseEntity<?> getCategories() {
        return VsResponseUtil.success(HttpStatus.OK, categoryService.getCategories());
    }

    @GetMapping(UrlConstant.Category.GET_CATEGORY)
    public ResponseEntity<?> getCategoryDetail(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, categoryService.getCategoryDetail(id));
    }

    // Admin
    @PostMapping(UrlConstant.Category.CREATE_CATEGORY)
    public ResponseEntity<?> createCategory(@RequestBody ReqCreateCategory req) {
        return VsResponseUtil.success(HttpStatus.CREATED, categoryService.createCategory(req));
    }

    @PutMapping(UrlConstant.Category.UPDATE_CATEGORY)
    public ResponseEntity<?> updateCategory(@RequestBody ReqUpdateCategory req) {
        return ResponseEntity.ok(categoryService.updateCategory(req));
    }

    @DeleteMapping(UrlConstant.Category.DELETE_CATEGORY)
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, categoryService.deleteCategory(id));
    }
}