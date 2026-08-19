package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateServiceReview;
import N18.haui.Pet_18.service.PetServiceReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
public class PetServiceReviewController {

    private final PetServiceReviewService reviewService;

    @PostMapping(UrlConstant.PetServiceReviews.CREATE_REVIEW)
    public ResponseEntity<?> createReview(@Valid @RequestBody ReqCreateServiceReview req) {
        return VsResponseUtil.success(HttpStatus.CREATED, reviewService.createReview(req));
    }

    @DeleteMapping(UrlConstant.PetServiceReviews.DELETE_REVIEW)
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return VsResponseUtil.success(HttpStatus.NO_CONTENT, null);
    }

    @GetMapping(UrlConstant.PetServiceReviews.GET_SERVICE_REVIEWS)
    public ResponseEntity<?> getServiceReviews(
            @PathVariable Long serviceId,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, reviewService.getServiceReviews(serviceId, pageable));
    }

    @GetMapping(UrlConstant.PetServiceReviews.GET_AVERAGE_RATING)
    public ResponseEntity<?> getAverageRating(@PathVariable Long serviceId) {
        return VsResponseUtil.success(HttpStatus.OK, reviewService.getAverageRating(serviceId));
    }

    @GetMapping(UrlConstant.PetServiceReviews.GET_REVIEW_COUNT)
    public ResponseEntity<?> getReviewCount(@PathVariable Long serviceId) {
        return VsResponseUtil.success(HttpStatus.OK, reviewService.getReviewCount(serviceId));
    }
}
