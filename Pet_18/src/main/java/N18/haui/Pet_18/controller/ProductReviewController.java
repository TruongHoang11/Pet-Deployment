package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProductReview;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProductReview;
import N18.haui.Pet_18.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService productReviewService;
    // ===== PUBLIC =====

    // Xem danh sách đánh giá theo sản phẩm
    @GetMapping(UrlConstant.ProductReview.GET_REVIEWS_BY_PRODUCT)
    public ResponseEntity<?> getReviewsByProduct(
            @PathVariable Long productId,
            Pageable pageable) {
        return VsResponseUtil.success(productReviewService.getReviewsByProduct(productId, pageable));
    }

    // ===== USER =====

    // Thêm đánh giá
    @PostMapping(UrlConstant.ProductReview.CREATE_REVIEW)
    public ResponseEntity<?> createReview(
            @RequestBody @Valid ReqCreateProductReview req) {
        return VsResponseUtil.success(productReviewService.createReview(req));
    }

    // Cập nhật đánh giá
    @PutMapping(UrlConstant.ProductReview.UPDATE_REVIEW)
    public ResponseEntity<?> updateReview(
            @RequestBody @Valid ReqUpdateProductReview req) {
        return VsResponseUtil.success(productReviewService.updateReview(req));
    }



    // Xóa đánh giá
    @DeleteMapping(UrlConstant.ProductReview.DELETE_REVIEW)
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        return VsResponseUtil.success(productReviewService.deleteReview(reviewId));
    }

    // ===== ADMIN =====

    // Admin xem tất cả đánh giá
    @GetMapping(UrlConstant.ProductReview.GET_ALL_REVIEWS)
    public ResponseEntity<?> getAllReviews(
            @RequestParam(required = false) List<String> filter,
            Pageable pageable) {
        return VsResponseUtil.success(productReviewService.getAllReviews(filter, pageable));
    }
}
