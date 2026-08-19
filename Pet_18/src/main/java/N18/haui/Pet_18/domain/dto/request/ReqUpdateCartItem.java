package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCartItem {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Số lượng tối thiểu là 1")
    private Integer quantity; // nếu quantity = 0 -> xoá item khỏi giỏ hàng
}
