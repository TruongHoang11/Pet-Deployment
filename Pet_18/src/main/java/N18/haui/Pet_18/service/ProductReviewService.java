package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProductReview;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProductReview;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductReviewDto;
import N18.haui.Pet_18.domain.dto.response.ProductReviewResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductReviewService {
    ProductReviewDto createReview(ReqCreateProductReview req);


    ProductReviewDto updateReview(ReqUpdateProductReview req);

    CommonResponseDto deleteReview(Long reviewId);

    ProductReviewResponseDto getReviewsByProduct(Long productId, Pageable pageable);


    ResultPaginationDto getAllReviews(List<String> filter, Pageable pageable);
}
