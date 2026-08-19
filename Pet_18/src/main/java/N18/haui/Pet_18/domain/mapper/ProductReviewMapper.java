package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.ProductReviewDto;
import N18.haui.Pet_18.domain.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {


        @Mapping(target = "userName", source = "user.name")
        @Mapping(target = "productId", source = "product.id")
        ProductReviewDto toDto(ProductReview productReview);

        List<ProductReviewDto> toDtoList(List<ProductReview> productReviews);

}
