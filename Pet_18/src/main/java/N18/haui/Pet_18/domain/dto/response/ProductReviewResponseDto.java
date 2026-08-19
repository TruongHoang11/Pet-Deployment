package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewResponseDto {
    private Double avgRating;
    private Integer totalReviews;
    private ResultPaginationDto reviews;
}