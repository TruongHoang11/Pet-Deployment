package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.response.CartItemDto;
import N18.haui.Pet_18.domain.entity.CartItem;
import N18.haui.Pet_18.domain.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
 //   @Mapping(target = "productImage", expression = "java(getMainImageUrl(product.getProductImages()))")
    @Mapping(target = "productImage", expression = "java(getMainImageUrl(cartItem.getProduct().getProductImages()))")
    //@Mapping(target = "totalPrice", expression = "java(cartItem.getQuantity().multiply(product.getPrice()))")
    @Mapping(target = "totalPrice", expression = "java(cartItem.getProduct().getPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())))")
    CartItemDto toDto(CartItem cartItem);

        default String getMainImageUrl(List<ProductImage> productImages){
        if(productImages == null){
            return null;
        }
        return productImages.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}

            //cartItem.getProduct().getPrice()
            /// / → Lấy giá sản phẩm, kiểu BigDecimal
            /// / VD: 150000.00
            //
            //cartItem.getQuantity()
            //// → Lấy số lượng trong giỏ, kiểu Integer
            //// VD: 3
            //
            //new java.math.BigDecimal(cartItem.getQuantity())
            //// → Convert Integer → BigDecimal
            //// Vì BigDecimal.multiply() chỉ nhận tham số BigDecimal
            //// VD: 3 → 3
            //
            //.multiply(...)
            //// → Nhân 2 BigDecimal với nhau
            //// VD: 150000.00 × 3 = 450000.00


            //// BigDecimal chỉ có method
            //BigDecimal.multiply(BigDecimal other) // ✅
            //
            /// / Không có
            //BigDecimal.multiply(Integer other)    // ❌ không tồn tại
