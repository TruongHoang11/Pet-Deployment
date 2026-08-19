package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDto {

    //id cart item
    private Long id;

    // hiển thị số lượng trong giỏ
    private Integer quantity;

    // thông tin product cần hiển thị
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImage;

    // tính toán tiền
    private BigDecimal totalPrice;
}
