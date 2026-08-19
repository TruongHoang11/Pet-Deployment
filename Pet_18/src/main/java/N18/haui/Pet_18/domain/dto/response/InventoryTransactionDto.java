package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InventoryTransactionDto extends UserDateAuditing {
   // private Long id;
    private Integer quantity;
    private String type; // export / import
    private String note; // ghi chu
    private Integer currentStock; // tồn kho sau khi import/ export
    private String productName; // tên sản phẩm
    private Long productId; // id sản phẩm
   // private String createdBy; // ai thực hiện
}
