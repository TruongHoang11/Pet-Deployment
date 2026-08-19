package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InventoryDto {
    private Long id;
    private Integer quantity; // tồn kho hiện tại
    private Long productId; // id sp
    private String productName; // tên sp
    private BigDecimal productPrice; // giá sp
}
