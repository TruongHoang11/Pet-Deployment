package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateServiceReview;
import N18.haui.Pet_18.domain.dto.response.ServiceReviewDto;
import org.springframework.data.domain.Pageable;

public interface PetServiceReviewService {

    ServiceReviewDto createReview(ReqCreateServiceReview req);

    void deleteReview(Long reviewId);

    ResultPaginationDto getServiceReviews(Long serviceId, Pageable pageable);

    Double getAverageRating(Long serviceId);

    Integer getReviewCount(Long serviceId);
}
