package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDetailDto {
    private Long id;              // ID chi tiết đơn hàng
    private Integer quantity;     // Số lượng sản phẩm đã đặt mua
    private BigDecimal unitPrice; // Giá bán 1 sản phẩm tại thời điểm mua
    private BigDecimal totalAmount;// Tổng tiền mục này (unitPrice * quantity)
    private Long productId;       // ID sản phẩm
    private String productName;   // Tên sản phẩm
    private String productImage;  // Đường dẫn URL ảnh sản phẩm
}
