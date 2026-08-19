package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReqUpdateOrderStatus {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Status is required")
    @EnumValue(name = "status", enumClass = OrderStatus.class)
    private String status;

    private String note;
}
