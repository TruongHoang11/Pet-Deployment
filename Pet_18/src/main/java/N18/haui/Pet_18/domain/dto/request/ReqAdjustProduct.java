package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqAdjustProduct {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "New quantity is required")
    private Integer newQuantity; // số lượng thực tế sau khi kiểm kê

    @NotBlank(message = "Note is required")
    private String note;
}
