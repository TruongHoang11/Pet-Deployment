package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.response.OrderDetailDto;
import N18.haui.Pet_18.domain.entity.OrderDetail;
import N18.haui.Pet_18.domain.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        imports = {java.math.BigDecimal.class}
)
public interface OrderDetailMapper {

    @Mapping(
            target = "totalAmount",
            expression = "java(orderDetail.getUnitPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())))"
    )
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(
            target = "productImage",
            expression = "java(getMainImageUrl(orderDetail.getProduct().getProductImages()))"
    )
    OrderDetailDto toDto(OrderDetail orderDetail);

    List<OrderDetailDto> toDtos(List<OrderDetail> orderDetails);

    default String getMainImageUrl(List<ProductImage> productImages) {
        if (productImages == null || productImages.isEmpty()) {
            return null;
        }

        return productImages.stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(productImages.get(0).getImageUrl());
    }
}
